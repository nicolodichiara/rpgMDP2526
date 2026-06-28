package it.unicam.cs.mpgc.rpg130669.domain.repository;

import it.unicam.cs.mpgc.rpg130669.domain.model.map.GameMap;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.world.WorldClock;

import java.util.Optional;

/**
 * Handles the output port for the full game save.
 * Implemented in the infrastructure.persistence.JSON layer.
 * ----
 * Optional is used to handle failed loading scenarios and avoid NullPointerExceptions.
 */
public interface SaveGameRepository {
    void save(Player player, GameMap currentMap, WorldClock clock);
    Optional<SaveGameSnapshot> load(String playerId);
    boolean hasSave(String playerId);
    void    deleteSave(String playerId);

    /**
     * Immutable snapshot of the loaded game state.
     * Used by GameSessionUseCase to reconstruct the session.
     */
    record SaveGameSnapshot(Player player, GameMap currentMap, WorldClock clock) {}
}
