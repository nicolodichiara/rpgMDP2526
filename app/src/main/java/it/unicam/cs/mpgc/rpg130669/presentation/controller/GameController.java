package it.unicam.cs.mpgc.rpg130669.presentation.controller;

import it.unicam.cs.mpgc.rpg130669.application.usecase.GameSessionUseCase;
import it.unicam.cs.mpgc.rpg130669.application.usecase.InventoryUseCase;
import it.unicam.cs.mpgc.rpg130669.domain.model.combat.FishingSession;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Stat;
import it.unicam.cs.mpgc.rpg130669.domain.repository.MapRepository;
import it.unicam.cs.mpgc.rpg130669.domain.service.VisibilityService;
import it.unicam.cs.mpgc.rpg130669.presentation.view.MapRenderer;
import it.unicam.cs.mpgc.rpg130669.presentation.viewmodel.GameViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Main controller of the game.
 * Coordinates: keyboard input → use case → ViewModel → MapRenderer.
 * Contains no domain logic — delegates everything to GameSessionUseCase.
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
    @FXML private Label fishLabel;


    private GameSessionUseCase session;
    private InventoryUseCase   inventoryUseCase;
    private GameViewModel      viewModel;
    private MapRenderer        mapRenderer;
    private MapRepository      mapRepository;


    // Selected position for casting (null if none)
    private Position selectedCastTarget = null;

    /**
     * Called by App.java after loading the FXML.
     */
    public void init(GameSessionUseCase session,
                     InventoryUseCase inventoryUseCase,
                     VisibilityService visibilityService,
                     MapRepository mapRepository) {
        this.session          = session;
        this.inventoryUseCase = inventoryUseCase;
        this.mapRepository    = mapRepository;
        this.viewModel        = new GameViewModel(session, visibilityService);

        setupMapRenderer();
        setupKeyboardInput();
        bindViewModel();
        viewModel.refresh();
    }


    /**
     * Sets up the map renderer, then allows selecting the Tile to cast the line.
     * Prints the selected tile to the console.
     * Click on a tile → selects it as the target for the cast.
     */

    private void setupMapRenderer() {
        mapRenderer = new MapRenderer();
        mapContainer.getChildren().add(mapRenderer);

        //
        mapRenderer.setOnMouseClicked(e -> {
            int col = (int)(e.getX() / mapRenderer.getTileSize());
            int row = (int)(e.getY() / mapRenderer.getTileSize());
            selectedCastTarget = new Position(row, col);
            appendLog("Tile selezionata: (" + row + ", " + col + ")");
        });
    }

    /**
     * Sets up keyboard input handling for JavaFX.
     */
    private void setupKeyboardInput() {
        // Il focus deve stare sul container per ricevere i tasti
        mapScrollPane.setOnKeyPressed(this::handleKey);
        mapScrollPane.setFocusTraversable(true);
        // Richiedi il focus dopo il render iniziale
        javafx.application.Platform.runLater(mapScrollPane::requestFocus);
    }

    /**
     * Manages the content of the displayed text, including the
     * clock, player, and fishCount.
     * Updates the map when the grid changes.
     */
    private void bindViewModel() {
        clockLabel .textProperty() .bind(viewModel.clockLabelProperty());
        playerLabel.textProperty().bind(viewModel.playerLabelProperty());
        fishLabel.textProperty().bind(viewModel.fishCountLabelProperty());

        viewModel.tileGridProperty().addListener((obs, old, grid) -> {
            var map = session.getCurrentMap();
            mapRenderer.render(grid,
                    map.getGrid().getRows(),
                    map.getGrid().getCols());
            updateStatBar();
            updateMapLabel();
        });
    }

    /**
     * Switch cases for handling keyboard input, mapping arrow key values to letter keys.
     * Calls position.translate to physically move the player to a new position.
     */
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
    // Tile is non-walkable or out of bounds - (does not print to console)
        }
        e.consume();
    }

    // FXML ACTION

    /**
     * Handles the execution of the 'Cast' button. If the position is non-null, it saves the session and then updates it.
     * If the saved session shows that the cast landed where a fish is present, combat begins.
     * Otherwise, it continues with the refreshed session.
     */
    @FXML
    private void onCast() {
        if (selectedCastTarget == null) {
            appendLog("Clicca sulla mappa per selezionare il punto di lancio.");
            return;
        }
        try {
            var maybeSession = session.cast(selectedCastTarget);
            viewModel.refresh();
            selectedCastTarget = null;

            if (maybeSession.isPresent()) {
                appendLog("Pesce agganciato! Inizia il combattimento.");
                openCombatWindow(maybeSession.get());   // ← passa l'oggetto, non solo il booleano
            } else {
                appendLog("Esca lanciata — nessun pesce su quella tile.");
            }
        } catch (Exception ex) {
            appendLog(" attenzione " + ex.getMessage());
        }
    }

    /**
     * Handles the execution of the 'Save' button.
     */
    @FXML
    private void onSave() {
        try {
            session.save();
            appendLog("Partita salvata.");
        } catch (Exception ex) {
            appendLog("Errore nel salvataggio: " + ex.getMessage());
        }
    }

    /**
     * Handles the execution of the 'Change Map' button. Loads the resource bundle for map selection.
     * If the map changes, it instantiates a new Stage.
     */
    @FXML
    private void onChangeMap() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unicam/cs/mpgc/rpg130669/fxml/map_selection.fxml"));
            Parent root = loader.load();

            MapSelectionController msc = loader.getController();
            msc.init(session, mapRepository, () -> {
                viewModel.refresh();
                appendLog("Mappa cambiata: " + session.getCurrentMap().getName());
            });

            Stage mapStage = new Stage();
            mapStage.initModality(Modality.WINDOW_MODAL);
            mapStage.initOwner(mapScrollPane.getScene().getWindow());
            mapStage.setTitle("Cambia Mappa");
            mapStage.setScene(new Scene(root));
            mapStage.setResizable(false);
            mapStage.showAndWait();

            mapScrollPane.requestFocus();
        } catch (Exception e) {
            appendLog("Errore nell'apertura del menu mappe: " + e.getMessage());
        }
    }

    /**
     * Loads the combat session, then calls the combat.fxml file,
     * instantiates the controller, and initializes it.
     * Once combat ends, it refreshes the view,
     * checks quests,
     * checks if all fish on the map have been caught,
     * and reloads the fishing session.
     */

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

                    if (session.consumeMapClearedFlag()) {
                        appendLog("Hai catturato tutti i pesci di " + session.getCurrentMap().getName()
                                + "! Usa 'Cambia mappa' per esplorare altrove.");
                    }
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
            appendLog("Errore nell'apertura del combattimento: " + e.getMessage());
        }
    }

    /**
     * Updates the stats in the output text display.
     */
    private void updateStatBar() {
        var player = session.getPlayer();
        perceptionLabel.setText(formatStat("PERC", Stat.PERCEPTION, player));
        castingLabel.setText(formatStat("CAST", Stat.CASTING, player));
        strengthLabel.setText(formatStat("STR",  Stat.STRENGTH,  player));
        patienceLabel.setText(formatStat("PAT",  Stat.PATIENCE,  player));
    }

    /**
     * @return a formatted string showing the completion percentage for the level stat
     */
    private String formatStat(String label, Stat stat, Player player) {
        int progressPercent = (int) Math.round(player.getStatProgress(stat) * 100);
        return label + ": " + player.getStat(stat) + " (" + progressPercent + "%)";
    }

    private void updateMapLabel() {
        mapLabel.setText(session.getCurrentMap().getName());
    }

    private void appendLog(String text) {
        eventLog.appendText(text + "\n");
    }


}
