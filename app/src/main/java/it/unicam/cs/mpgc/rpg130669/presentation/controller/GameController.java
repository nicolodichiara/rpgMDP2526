package it.unicam.cs.mpgc.rpg130669.presentation.controller;

import it.unicam.cs.mpgc.rpg130669.application.usecase.GameSessionUseCase;
import it.unicam.cs.mpgc.rpg130669.application.usecase.InventoryUseCase;
import it.unicam.cs.mpgc.rpg130669.domain.model.combat.FishingSession;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Stat;
import it.unicam.cs.mpgc.rpg130669.domain.service.VisibilityService;
import it.unicam.cs.mpgc.rpg130669.presentation.view.MapRenderer;
import it.unicam.cs.mpgc.rpg130669.presentation.viewmodel.GameViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller principale del gioco.
 * Coordina: input tastiera → use case → ViewModel → MapRenderer.
 * Non contiene logica di dominio — delega tutto al GameSessionUseCase.
 */
public class GameController {

    @FXML private Label     clockLabel;
    @FXML private Label     playerLabel;
    @FXML private Label     mapLabel;
    @FXML private Label     perceptionLabel;
    @FXML private Label     castingLabel;
    @FXML private Label     strengthLabel;
    @FXML private Label     patienceLabel;
    @FXML private TextArea  eventLog;
    @FXML private Button    castButton;
    @FXML private StackPane mapContainer;
    @FXML private ScrollPane mapScrollPane;

    private GameSessionUseCase session;
    private InventoryUseCase   inventoryUseCase;
    private GameViewModel      viewModel;
    private MapRenderer        mapRenderer;

    // Posizione selezionata per il cast (null se nessuna)
    private Position selectedCastTarget = null;

    /**
     * Chiamato da App.java dopo aver caricato l'FXML.
     */
    public void init(GameSessionUseCase session,
                     InventoryUseCase inventoryUseCase,
                     VisibilityService visibilityService) {
        this.session          = session;
        this.inventoryUseCase = inventoryUseCase;
        this.viewModel        = new GameViewModel(session, visibilityService);

        setupMapRenderer();
        setupKeyboardInput();
        bindViewModel();
        viewModel.refresh();
    }

    // ── setup ─────────────────────────────────────────────────────────────────

    private void setupMapRenderer() {
        mapRenderer = new MapRenderer();
        mapContainer.getChildren().add(mapRenderer);

        // Click su una tile → seleziona come target del cast
        mapRenderer.setOnMouseClicked(e -> {
            int col = (int)(e.getX() / mapRenderer.getTileSize());
            int row = (int)(e.getY() / mapRenderer.getTileSize());
            selectedCastTarget = new Position(row, col);
            appendLog("Tile selezionata: (" + row + ", " + col + ")");
        });
    }

    private void setupKeyboardInput() {
        // Il focus deve stare sul container per ricevere i tasti
        mapScrollPane.setOnKeyPressed(this::handleKey);
        mapScrollPane.setFocusTraversable(true);
        // Richiedi il focus dopo il render iniziale
        javafx.application.Platform.runLater(mapScrollPane::requestFocus);
    }

    private void bindViewModel() {
        clockLabel .textProperty() .bind(viewModel.clockLabelProperty());
        playerLabel.textProperty().bind(viewModel.playerLabelProperty());

        // Aggiorna mappa quando la griglia cambia
        viewModel.tileGridProperty().addListener((obs, old, grid) -> {
            var map = session.getCurrentMap();
            mapRenderer.render(grid,
                    map.getGrid().getRows(),
                    map.getGrid().getCols());
            updateStatBar();
            updateMapLabel();
        });
    }

    // ── input ─────────────────────────────────────────────────────────────────

    private void handleKey(KeyEvent e) {
        if (session.getActiveSession() != null
                && session.getActiveSession().isActive()) return;

        Position current = session.getPlayer().getPosition();
        Position dest    = switch (e.getCode()) {
            case W, UP    -> current.translate(-1,  0);
            case S, DOWN  -> current.translate( 1,  0);
            case A, LEFT  -> current.translate( 0, -1);
            case D, RIGHT -> current.translate( 0,  1);
            default       -> null;
        };

        if (dest == null) return;

        try {
            session.movePlayer(dest);
            viewModel.refresh();
        } catch (IllegalArgumentException ex) {
            // Tile non camminabile o fuori mappa — silenzioso
        }
        e.consume();
    }

    // ── azioni FXML ──────────────────────────────────────────────────────────

    @FXML
    private void onCast() {
        if (selectedCastTarget == null) {
            appendLog("⚠ Clicca sulla mappa per selezionare il punto di lancio.");
            return;
        }
        try {
            var maybeSession = session.cast(selectedCastTarget);
            viewModel.refresh();
            selectedCastTarget = null;

            if (maybeSession.isPresent()) {
                appendLog("🎣 Pesce agganciato! Inizia il combattimento.");
                openCombatWindow(maybeSession.get());   // ← passa l'oggetto, non solo il booleano
            } else {
                appendLog("🌊 Esca lanciata — nessun pesce su quella tile.");
            }
        } catch (Exception ex) {
            appendLog("⚠ " + ex.getMessage());
        }
    }


    @FXML
    private void onSave() {
        try {
            session.save();
            appendLog("💾 Partita salvata.");
        } catch (Exception ex) {
            appendLog("⚠ Errore nel salvataggio: " + ex.getMessage());
        }
    }

    // ── finestra combattimento ────────────────────────────────────────────────

    private void openCombatWindow(FishingSession fishingSession) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unicam/cs/mpgc/rpg130669/fxml/combat.fxml"));
            Parent root = loader.load();

            CombatController cc = loader.getController();
            cc.init(session, inventoryUseCase, viewModel, fishingSession, () -> {
                javafx.application.Platform.runLater(() -> {
                    Stage s = (Stage) root.getScene().getWindow();
                    s.close();
                    viewModel.refresh();
                    appendLog("Combattimento concluso.");
                    mapScrollPane.requestFocus();
                });
            });

            Stage combatStage = new Stage();
            combatStage.initModality(Modality.WINDOW_MODAL);
            combatStage.initOwner(mapScrollPane.getScene().getWindow());
            combatStage.setTitle("Sessione di Pesca");
            combatStage.setScene(new Scene(root));
            combatStage.setResizable(false);
            combatStage.show();

        } catch (Exception e) {
            appendLog("⚠ Errore nell'apertura del combattimento: " + e.getMessage());
        }
    }
    // ── aggiornamento UI ──────────────────────────────────────────────────────

    private void updateStatBar() {
        var player = session.getPlayer();
        perceptionLabel.setText("PERC: " + player.getStat(Stat.PERCEPTION));
        castingLabel.setText("CAST: "    + player.getStat(Stat.CASTING));
        strengthLabel.setText("STR: "    + player.getStat(Stat.STRENGTH));
        patienceLabel.setText("PAT: "    + player.getStat(Stat.PATIENCE));
    }

    private void updateMapLabel() {
        mapLabel.setText(session.getCurrentMap().getName());
    }

    private void appendLog(String text) {
        eventLog.appendText(text + "\n");
    }


}
