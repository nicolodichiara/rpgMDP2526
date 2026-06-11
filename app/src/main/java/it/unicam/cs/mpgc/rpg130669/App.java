package it.unicam.cs.mpgc.rpg130669;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Fishing RPG — in costruzione");
        Scene scene = new Scene(new StackPane(label), 900, 650);

        stage.setTitle("Fishing RPG");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}