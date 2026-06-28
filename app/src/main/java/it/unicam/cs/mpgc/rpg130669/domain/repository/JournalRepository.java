package it.unicam.cs.mpgc.rpg130669.domain.repository;

import java.util.List;
import java.util.Optional;

/**
 * Handles the output port for the fishing journal.
 * Implemented in the infrastructure.persistence.SQLite layer.
 * ----
 * Optional is used to handle failed loading scenarios and avoid NullPointerExceptions.
 */
public interface JournalRepository {
    /** Logs a new catch. */
    void recordCatch(String playerId, String fishId, int weight);

    /** Total number of catches for a specific species. */
    int getCatchCount(String playerId, String fishId);

    /** Maximum weight caught for a specific species (personal record). */
    Optional<Integer> getRecord(String playerId, String fishId);

    /** All species caught at least once by this player. */
    List<String> getDiscoveredFishIds(String playerId);

    /** True if the player has previously caught this species. */
    boolean hasDiscovered(String playerId, String fishId);
}
