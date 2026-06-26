package it.unicam.cs.mpgc.rpg130669;

import it.unicam.cs.mpgc.rpg130669.domain.model.item.FishingRod;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.GameMap;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.infrastructure.config.AppContext;
import it.unicam.cs.mpgc.rpg130669.presentation.controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.List;
/**
 * punto di partenza del progetto, che si basa su AppContext
 */
public class App extends Application {

    private AppContext context;


    /**
     * set-up per l'inizio del gioco, vede se presente un salvataggio e in caso carica la schermata.
     * in caso di nuova partita genera un
     * player nella posizione 2,2
     * fishingRod base.
     * Infine carica il gioco
     */
    @Override
    public void start(Stage stage) throws Exception {
        context = new AppContext();
        String playerId = "p1";

        boolean continuing = context.getSaveGameRepository().hasSave(playerId)
                && askContinueOrNewGame();

        if (continuing) {
            context.getGameSessionUseCase().loadGame(playerId, List.of());
        } else {
            Player  player   = new Player(playerId, "Pescatore", new Position(2, 2));
            GameMap startMap = context.getMapRepository()
                    .loadById(1)
                    .orElseThrow(() -> new RuntimeException("map_001.xml non trovata"));
            context.getGameSessionUseCase().startNewGame(player, startMap, List.of());

            var rod = new FishingRod("rod_start", "Canna del Principiante", "desc", 80, 4, 4);
            context.getInventoryUseCase().addItem(rod, player, 1);
        }

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/it/unicam/cs/mpgc/rpg130669/fxml/game.fxml"));
        Parent root = loader.load();

        GameController gc = loader.getController();
        gc.init(context.getGameSessionUseCase(),
                context.getInventoryUseCase(),
                context.getVisibilityService(),
                context.getMapRepository());
        stage.setTitle("Fishing RPG");
        stage.setScene(new Scene(root, 1100, 700));
        stage.show();
    }

    /**
     * nova partita / carica salvataggio
     */
    private boolean askContinueOrNewGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Fishing RPG");
        alert.setHeaderText("È stato trovato un salvataggio.");
        alert.setContentText("Vuoi continuare la partita o iniziarne una nuova?");

        ButtonType continueBtn = new ButtonType("Continua");
        ButtonType newGameBtn  = new ButtonType("Nuova partita");
        alert.getButtonTypes().setAll(continueBtn, newGameBtn);

        return alert.showAndWait()
                .filter(response -> response == continueBtn)
                .isPresent();
    }

    @Override
    public void stop() {
        if (context != null) context.shutdown();
    }

    public static void main(String[] args) { launch(args); }
}