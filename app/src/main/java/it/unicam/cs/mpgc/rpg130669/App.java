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
import javafx.stage.Stage;

import java.util.List;

public class App extends Application {

    private AppContext context;

    @Override
    public void start(Stage stage) throws Exception {
        context = new AppContext();

        // ── bootstrap partita di test ────────────────────────────────────────
        Player  player   = new Player("p1", "Pescatore", new Position(2, 2));
        System.out.println("Posizione player appena creato: " + player.getPosition());
        GameMap startMap = context.getMapRepository()
                .loadById(1)
                .orElseThrow(() -> new RuntimeException("map_001.xml non trovata"));

        context.getGameSessionUseCase()
                .startNewGame(player, startMap, List.of());

        // Dai al giocatore una canna di partenza
        var rod = new FishingRod("rod_start", "Canna del Principiante", "desc", 80, 4, 4);
        context.getInventoryUseCase().addItem(rod, player, 1);

        // ── carica FXML ──────────────────────────────────────────────────────
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/it/unicam/cs/mpgc/rpg130669/fxml/game.fxml"));
        Parent root = loader.load();

        GameController gc = loader.getController();
        gc.init(context.getGameSessionUseCase(),
                context.getInventoryUseCase(),
                context.getVisibilityService());

        stage.setTitle("Fishing RPG");
        stage.setScene(new Scene(root, 1100, 700));
        stage.show();
    }

    @Override
    public void stop() {
        if (context != null) context.shutdown();
    }

    public static void main(String[] args) { launch(args); }
}