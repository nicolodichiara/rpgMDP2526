package it.unicam.cs.mpgc.rpg130669.domain.repository;

import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;

import java.util.Optional;

/**
 * Handles the output port for player data.
 * Implemented in the infrastructure.persistence.json layer.
 * ----
 * Optional is used to handle failed loading scenarios and avoid NullPointerExceptions.
 */
public interface CharacterRepository {
    void   save(Player player);
    Optional<Player> load(String playerId);
    boolean exists(String playerId);
    void   delete(String playerId);
}
