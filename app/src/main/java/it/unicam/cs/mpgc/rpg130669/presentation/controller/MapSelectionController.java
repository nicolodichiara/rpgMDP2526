package it.unicam.cs.mpgc.rpg130669.presentation.controller;

import it.unicam.cs.mpgc.rpg130669.application.usecase.GameSessionUseCase;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.GameMap;
import it.unicam.cs.mpgc.rpg130669.domain.repository.MapRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * Map selection screen. Displays all available maps,
 * indicating which ones are accessible based on the player's level.
 */
public class MapSelectionController {

    @FXML private ListView<GameMap> mapListView;
    @FXML private Label             infoLabel;
    @FXML private Button            goButton;

    private GameSessionUseCase session;
    private Runnable           onMapChanged;

    public void init(GameSessionUseCase session, MapRepository mapRepository,
                     Runnable onMapChanged) {
        this.session      = session;
        this.onMapChanged = onMapChanged;

        List<GameMap> maps = mapRepository.loadAll();
        mapListView.getItems().setAll(maps);
        mapListView.setCellFactory(list -> new MapCell());

        mapListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> updateInfo(selected));

        goButton.setDisable(true);

        maps.stream()
                .filter(m -> m.getLevelId() == session.getCurrentMap().getLevelId())
                .findFirst()
                .ifPresent(m -> mapListView.getSelectionModel().select(m));
    }

    private void updateInfo(GameMap map) {
        if (map == null) { goButton.setDisable(true); return; }

        boolean isCurrent  = map.getLevelId() == session.getCurrentMap().getLevelId();
        boolean isUnlocked = session.getPlayer().canAccessMap(map.getRequiredLevel());

        if (isCurrent) {
            infoLabel.setText("Sei già in questa mappa.");
        } else if (!isUnlocked) {
            infoLabel.setText("Richiede livello " + map.getRequiredLevel()
                    + " (sei al livello " + session.getPlayer().getLevel() + ")");
        } else {
            infoLabel.setText("Pesci presenti: " + map.getActiveFish().size());
        }

        goButton.setDisable(isCurrent || !isUnlocked);
    }

    @FXML
    private void onGo() {
        GameMap selected = mapListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        boolean success = session.changeMap(selected);
        if (success && onMapChanged != null) onMapChanged.run();
        closeWindow();
    }

    @FXML
    private void onCancel() { closeWindow(); }

    private void closeWindow() {
        ((Stage) mapListView.getScene().getWindow()).close();
    }

    /** Custom cell: map name + lock indicator. */
    private class MapCell extends ListCell<GameMap> {
        @Override
        protected void updateItem(GameMap map, boolean empty) {
            super.updateItem(map, empty);
            if (empty || map == null) { setText(null); return; }
            boolean unlocked = session.getPlayer().canAccessMap(map.getRequiredLevel());
            setText((unlocked ? "🌊 " : "🔒 ") + map.getName()
                    + "  (Lv. " + map.getRequiredLevel() + ")");
        }
    }
}
