package it.unicam.cs.mpgc.rpg130669.presentation.viewmodel;

import it.unicam.cs.mpgc.rpg130669.application.usecase.GameSessionUseCase;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishEntity;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.GameMap;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.world.WorldClock;
import it.unicam.cs.mpgc.rpg130669.domain.service.VisibilityService;
import javafx.beans.property.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Stato osservabile della sessione di gioco.
 * Il GameController aggiorna queste property dopo ogni azione —
 * il MapRenderer le legge per ridisegnarsi.
 *
 * Usa JavaFX Properties invece di callback manuali:
 * il binding è dichiarativo e la UI si aggiorna automaticamente.
 */
public class GameViewModel {

    private final GameSessionUseCase  session;
    private final VisibilityService   visibilityService;

    // ── property osservabili ─────────────────────────────────────────────────

    private final StringProperty  clockLabel   = new SimpleStringProperty("");
    private final StringProperty  playerLabel  = new SimpleStringProperty("");
    private final IntegerProperty playerLevel  = new SimpleIntegerProperty(0);
    private final BooleanProperty combatActive = new SimpleBooleanProperty(false);

    /** Snapshot della griglia — ridisegnato dopo ogni azione. */
    private final ObjectProperty<Map<Position, TileViewModel>> tileGrid =
            new SimpleObjectProperty<>(Map.of());

    public GameViewModel(GameSessionUseCase session, VisibilityService visibilityService) {
        this.session           = session;
        this.visibilityService = visibilityService;
    }

    /**
     * Aggiorna tutte le property leggendo lo stato corrente dal GameSessionUseCase.
     * Chiamato da GameController dopo ogni azione del giocatore.
     */
    public void refresh() {
        Player    player = session.getPlayer();
        GameMap   map    = session.getCurrentMap();
        WorldClock clock = session.getClock();

        var grid = buildTileGrid(map, player);
        System.out.println("Player pos: " + player.getPosition() + " | tile alla pos: " + grid.get(player.getPosition()));
        tileGrid.set(grid);

        clockLabel .set("Giorno " + clock.getDay() + " — " + clock.getTimeOfDay().getCode());
        playerLabel.set(player.getName() + "  Lv." + player.getLevel());
        playerLevel.set(player.getLevel());
        combatActive.set(session.getActiveSession() != null
                && session.getActiveSession().isActive());

        tileGrid.set(buildTileGrid(map, player));
    }

    private Map<Position, TileViewModel> buildTileGrid(GameMap map, Player player) {
        Map<Position, TileViewModel> grid = new HashMap<>();
        Position playerPos = player.getPosition();

        // Indice veloce posizione → pesce
        Map<Position, FishEntity> fishIndex = new HashMap<>();
        for (FishEntity f : map.getActiveFish())
            fishIndex.put(f.getPosition(), f);

        for (Position pos : map.getGrid().getAllPositions()) {
            boolean  hasPlayer = pos.equals(playerPos);
            FishEntity fish    = fishIndex.get(pos);

            TileViewModel vm = fish != null
                    ? new TileViewModel(
                    map.getTile(pos).type(),
                    hasPlayer,
                    true,
                    visibilityService.compute(fish, player),
                    fish.getBehaviorState())
                    : new TileViewModel(
                    map.getTile(pos).type(),
                    hasPlayer,
                    false,
                    null,
                    null);

            grid.put(pos, vm);
        }
        return grid;
    }

    // ── getter property ──────────────────────────────────────────────────────

    public StringProperty  clockLabelProperty()   { return clockLabel;   }
    public StringProperty  playerLabelProperty()  { return playerLabel;  }
    public IntegerProperty playerLevelProperty()  { return playerLevel;  }
    public BooleanProperty combatActiveProperty() { return combatActive; }
    public ObjectProperty<Map<Position, TileViewModel>> tileGridProperty() { return tileGrid; }
}