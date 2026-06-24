package it.unicam.cs.mpgc.rpg130669.infrastructure.config;

import it.unicam.cs.mpgc.rpg130669.application.CombatEngine;
import it.unicam.cs.mpgc.rpg130669.application.FishBehaviorEngine;
import it.unicam.cs.mpgc.rpg130669.application.usecase.*;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.*;
import it.unicam.cs.mpgc.rpg130669.domain.repository.*;
import it.unicam.cs.mpgc.rpg130669.domain.service.*;
import it.unicam.cs.mpgc.rpg130669.infrastructure.persistence.db.*;
import it.unicam.cs.mpgc.rpg130669.infrastructure.persistence.json.*;
import it.unicam.cs.mpgc.rpg130669.infrastructure.persistence.xml.*;

import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

/**
 * Composition root dell'applicazione.
 * Costruisce e collega tutte le dipendenze in ordine corretto.
 * App.java crea una sola istanza di AppContext e la usa per
 * ottenere GameSessionUseCase — nient'altro.
 */
public class AppContext {

    private final DatabaseManager       databaseManager;
    private final MapRepository         mapRepository;
    private final SaveGameRepository    saveGameRepository;
    private final JournalRepository     journalRepository;
    private final GameSessionUseCase    gameSessionUseCase;
    private final InventoryUseCase      inventoryUseCase;
    private final VisibilityService     visibilityService;

    public AppContext() {
        try {
            // ── infrastruttura base ──────────────────────────────────────────
            databaseManager = new DatabaseManager();

            // ── templates dei pesci (hardcoded V1, in V2 da XML) ────────────
            Map<String, FishTemplate> fishTemplates = buildFishTemplates();

            // ── repository ───────────────────────────────────────────────────
            mapRepository      = new XmlMapRepository(fishTemplates);
            saveGameRepository = new JsonSaveGameRepository(mapRepository);
            journalRepository  = new SqliteJournalRepository(databaseManager);

            // ── domain service ───────────────────────────────────────────────
            visibilityService  = new VisibilityService();
            SpawnService spawnService = new SpawnService(new Random());

            // ── use case ─────────────────────────────────────────────────────
            inventoryUseCase = new InventoryUseCase();
            QuestUseCase     questUseCase = new QuestUseCase(journalRepository);
            CombatEngine     combatEngine = new CombatEngine(new Random());
            FishBehaviorEngine behaviorEngine = new FishBehaviorEngine(new Random());

            gameSessionUseCase = new GameSessionUseCase(
                    behaviorEngine,
                    combatEngine,
                    inventoryUseCase,
                    questUseCase,
                    spawnService,
                    saveGameRepository,
                    journalRepository
            );

        } catch (SQLException e) {
            throw new RuntimeException("Errore di inizializzazione del database", e);
        }
    }

    // ── getter per la presentation layer ─────────────────────────────────────

    public GameSessionUseCase getGameSessionUseCase() { return gameSessionUseCase; }
    public InventoryUseCase   getInventoryUseCase()   { return inventoryUseCase;   }
    public MapRepository      getMapRepository()      { return mapRepository;      }
    public JournalRepository  getJournalRepository()  { return journalRepository;  }
    public VisibilityService  getVisibilityService()  { return visibilityService;  }
    public SaveGameRepository getSaveGameRepository() { return saveGameRepository; }

    public void shutdown() {
        try { databaseManager.close(); }
        catch (SQLException e) { System.err.println("Errore chiusura DB: " + e.getMessage()); }
    }

    // ── fish templates V1 ─────────────────────────────────────────────────────

    private Map<String, FishTemplate> buildFishTemplates() {
        BehaviorProfile common = new BehaviorProfile(0.5f, 0.3f, 0.3f, 5, 1);
        BehaviorProfile rare   = new BehaviorProfile(0.2f, 0.7f, 0.2f, 4, 2);

        return Map.of(
                "trota",   new FishTemplate("trota",   "Trota",   FishRarity.COMMON,   common, 20, 20, 3, 3),
                "carpa",   new FishTemplate("carpa",   "Carpa",   FishRarity.COMMON,   common, 25, 25, 2, 2),
                "salmone", new FishTemplate("salmone", "Salmone", FishRarity.UNCOMMON, rare,   30, 30, 5, 6),
                "luccio",  new FishTemplate("luccio",  "Luccio",  FishRarity.RARE,     rare,   40, 35, 7, 10)
        );
    }
}