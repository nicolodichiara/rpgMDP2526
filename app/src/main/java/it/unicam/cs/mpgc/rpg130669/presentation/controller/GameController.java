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
    @FXML private Label fishLabel;


    private GameSessionUseCase session;
    private InventoryUseCase   inventoryUseCase;
    private GameViewModel      viewModel;
    private MapRenderer        mapRenderer;
    private MapRepository      mapRepository;


    // Posizione selezionata per il cast (null se nessuna)
    private Position selectedCastTarget = null;

    /**
     * Chiamato da App.java dopo aver caricato l'FXML.
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
     * fa il setup per il map render, dopo permette di selezionare la Tile dove eseguire il lancio
     * stampa a console la tile selezionata.
     * Click su una tile → seleziona come target del cast
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
     * setup per l'interpretazione degli input da tastiera per javaFx
     */
    private void setupKeyboardInput() {
        // Il focus deve stare sul container per ricevere i tasti
        mapScrollPane.setOnKeyPressed(this::handleKey);
        mapScrollPane.setFocusTraversable(true);
        // Richiedi il focus dopo il render iniziale
        javafx.application.Platform.runLater(mapScrollPane::requestFocus);
    }

    /**
     * gestisce il contenuto del testo visualizzato, del
     * clock, player, fishCount
     * aggiorna la mappa quando la griglia cambia
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
     * case per la gestione dell'input da tastiera, che associa i valori delle frecce a quelli delle lettere
     * chiamano position.traslate per spostare fisicamente il giocatore a una nuova posizione
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
            // Tile non camminabile o fuori mappa - (non esce a console)
        }
        e.consume();
    }

    // AZIONI FXML

    /**
     * gestione dell'esecuizione del btn 'lancia', se la posizione è non null, salva la sessione e successivamente la aggiorna.
     * Se nella sessione salvata, risulta che il lancio è avvenuto dove c'è un pesce inizia il combattimento.
     * sennò si continua con la sessione refreshata
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
     * gestione dell'esecuzione del btn 'save'
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
     * gestione dell'esecuzione del btn 'cambia mappa'. Carica il pacchetto delle risorse per la selezione delle map
     * se cambia la mappa di istanzia un nuovo Stage.
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
     * Carica la sessione di combattimento, quindi chiama il file combat.fxml
     * istanzia il controller e ne fa l'init.
     * finito il combattimento fa il refresh,
     * controlla le quest,
     * controlla se avvenuta la cattura di tutti i pesci della mappa
     * ri-carica la sessione di pesca
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
     * update delle statistiche nella scritta a output
     */
    private void updateStatBar() {
        var player = session.getPlayer();
        perceptionLabel.setText(formatStat("PERC", Stat.PERCEPTION, player));
        castingLabel.setText(formatStat("CAST", Stat.CASTING, player));
        strengthLabel.setText(formatStat("STR",  Stat.STRENGTH,  player));
        patienceLabel.setText(formatStat("PAT",  Stat.PATIENCE,  player));
    }

    /**
     * @return stringa formattata con la percentuale per il superamento del livello per la stat
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
