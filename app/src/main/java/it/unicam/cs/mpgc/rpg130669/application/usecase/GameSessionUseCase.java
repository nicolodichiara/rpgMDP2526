package it.unicam.cs.mpgc.rpg130669.application.usecase;

import it.unicam.cs.mpgc.rpg130669.application.CombatEngine;
import it.unicam.cs.mpgc.rpg130669.application.FishBehaviorEngine;
import it.unicam.cs.mpgc.rpg130669.domain.model.combat.FishingSession;
import it.unicam.cs.mpgc.rpg130669.domain.model.combat.PlayerAction;
import it.unicam.cs.mpgc.rpg130669.domain.model.combat.SessionState;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishEntity;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.FishingRod;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.Item;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.GameMap;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.quest.Quest;
import it.unicam.cs.mpgc.rpg130669.domain.model.world.WorldClock;
import it.unicam.cs.mpgc.rpg130669.domain.repository.JournalRepository;
import it.unicam.cs.mpgc.rpg130669.domain.repository.SaveGameRepository;
import it.unicam.cs.mpgc.rpg130669.domain.service.SpawnService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Orchestratore principale del loop di gioco.
 * Ogni metodo pubblico rappresenta un'azione del giocatore e segue
 * sempre lo stesso schema:
 *   1. valida la precondizione
 *   2. applica l'effetto sul dominio
 *   3. aggiorna FishBehaviorEngine
 *   4. avanza il WorldClock
 *   5. verifica quest
 */
public class GameSessionUseCase {

    private final FishBehaviorEngine fishBehaviorEngine;
    private final CombatEngine combatEngine;
    private final InventoryUseCase inventoryUseCase;
    private final QuestUseCase questUseCase;
    private final SpawnService       spawnService;
    private final SaveGameRepository saveRepo;
    private final JournalRepository  journalRepo;

    // ── stato corrente di sessione ────────────────────────────────────────────
    private Player          player;
    private GameMap         currentMap;
    private WorldClock      clock;
    private FishingSession  activeSession;
    private Position        lastCastPosition;
    private List<Quest>     activeQuests;
    private boolean         mapJustCleared = false;

    public GameSessionUseCase(FishBehaviorEngine fishBehaviorEngine,
                              CombatEngine combatEngine,
                              InventoryUseCase inventoryUseCase,
                              QuestUseCase questUseCase,
                              SpawnService spawnService,
                              SaveGameRepository saveRepo,
                              JournalRepository journalRepo) {
        this.fishBehaviorEngine = fishBehaviorEngine;
        this.combatEngine       = combatEngine;
        this.inventoryUseCase   = inventoryUseCase;
        this.questUseCase       = questUseCase;
        this.spawnService       = spawnService;
        this.saveRepo           = saveRepo;
        this.journalRepo        = journalRepo;
    }

    // ── inizializzazione ──────────────────────────────────────────────────────

    public void startNewGame(Player player, GameMap startMap, List<Quest> quests) {
        this.player       = Objects.requireNonNull(player);
        this.currentMap   = Objects.requireNonNull(startMap);
        this.clock        = WorldClock.newGame();
        this.activeQuests = quests;
        spawnService.populate(currentMap, clock);
    }

    public boolean loadGame(String playerId, List<Quest> quests) {
        Optional<SaveGameRepository.SaveGameSnapshot> snap = saveRepo.load(playerId);
        if (snap.isEmpty()) return false;
        this.player       = snap.get().player();
        this.currentMap   = snap.get().currentMap();   // ← non serve più passarla da fuori
        this.clock         = snap.get().clock();
        this.activeQuests = quests;
        spawnService.populate(currentMap, clock);
        return true;
    }

    // ── azioni del giocatore ──────────────────────────────────────────────────

    /**
     * Muove il giocatore in una posizione adiacente.
     * @throws IllegalArgumentException se la destinazione non è walkable o adiacente
     */
    public void movePlayer(Position destination) {
        requireNoActiveSession();
        if (!currentMap.isValid(destination))
            throw new IllegalArgumentException("Posizione fuori mappa: " + destination);
        if (!currentMap.isWalkable(destination))
            throw new IllegalArgumentException("Tile non camminabile: " + destination);
        if (!player.getPosition().adjacentPosition(destination))
            throw new IllegalArgumentException("Destinazione non adiacente");

        player.setPosition(destination);
        afterAction();
    }

    /**
     * Lancia l'esca su una tile d'acqua entro il range della canna.
     * Avvia una FishingSession se c'è un pesce sulla tile bersaglio.
     */
    public Optional<FishingSession> cast(Position target) {
        requireNoActiveSession();
        if (!currentMap.isFishable(target))
            throw new IllegalArgumentException("Non puoi lanciare qui");

        FishingRod rod = inventoryUseCase.getEquippedRod(player);
        if (player.getPosition().distanceTo(target) > rod.getRange())
            throw new IllegalArgumentException("Fuori portata della canna");

        lastCastPosition = target;

        Optional<FishEntity> fishOnTile = currentMap.getActiveFish().stream()
                .filter(f -> f.getPosition().equals(target))
                .findFirst();

        if (fishOnTile.isPresent()) {
            activeSession = new FishingSession(player, fishOnTile.get(), rod);
            activeSession.startCombat();
        }

        afterAction();
        return Optional.ofNullable(activeSession);
    }

    /**
     * Esegue un'azione del giocatore nel combattimento attivo.
     * Dopo l'azione del giocatore, risolve automaticamente il turno del pesce.
     */
    public void performCombatAction(PlayerAction action, Item usedItem) {
        requireActiveSession();
        combatEngine.applyPlayerAction(activeSession, action, usedItem);

        if (activeSession.isFishTurn())
            combatEngine.applyFishTurn(activeSession);

        if (activeSession.isConcluded())
            concludeSession();

        afterAction();
    }

    // ── navigazione tra mappe ─────────────────────────────────────────────────

    public boolean changeMap(GameMap newMap) {
        if (!player.canAccessMap(newMap.getRequiredLevel())) return false;
        currentMap       = newMap;
        player.setPosition(newMap.getDefaultSpawnPosition());   // ← evita posizioni fuori griglia
        lastCastPosition = null;
        mapJustCleared   = false;
        spawnService.populate(currentMap, clock);
        return true;
    }

    // ── persistenza ───────────────────────────────────────────────────────────

    public void save() {
        saveRepo.save(player, currentMap, clock);
    }

    // ── getter per la presentation layer ─────────────────────────────────────

    public Player         getPlayer()        { return player;        }
    public GameMap        getCurrentMap()    { return currentMap;    }
    public WorldClock     getClock()         { return clock;         }
    public FishingSession getActiveSession() { return activeSession; }
    public List<Quest>    getActiveQuests()  { return activeQuests;  }

    // ── privati ───────────────────────────────────────────────────────────────

    /** Operazioni comuni dopo ogni azione: fish AI, clock, quest. */
    private void afterAction() {
        fishBehaviorEngine.update(currentMap, player.getPosition(), lastCastPosition);
        clock.advance();
        questUseCase.checkAndComplete(player, activeQuests);
    }

    private void concludeSession() {
        if (activeSession.getSessionState() == SessionState.CAUGHT) {
            FishEntity fish = activeSession.getTargetFish();
            try {
                journalRepo.recordCatch(player.getId(), fish.getTemplate().id(), 0);
            } catch (RuntimeException e) {
                System.err.println("⚠ Impossibile registrare la cattura nel journal: " + e.getMessage());
            }
            currentMap.removeFish(fish);

            if (currentMap.isCleared()) {
                mapJustCleared = true;   // ← si alza esattamente al momento della transizione
            }
        }
        activeSession    = null;
        lastCastPosition = null;
    }

    /**
     * Consuma il flag di "livello completato": ritorna true solo la prima
     * volta che viene letto dopo che l'ultimo pesce della mappa è stato
     * catturato. La presentation layer lo controlla dopo ogni combattimento.
     */
    public boolean consumeMapClearedFlag() {
        boolean wasCleared = mapJustCleared;
        mapJustCleared = false;
        return wasCleared;
    }

    private void requireActiveSession() {
        if (activeSession == null || !activeSession.isActive())
            throw new IllegalStateException("Nessuna sessione di pesca attiva");
    }

    private void requireNoActiveSession() {
        if (activeSession != null && activeSession.isActive())
            throw new IllegalStateException("C'è già una sessione di pesca attiva");
    }


}