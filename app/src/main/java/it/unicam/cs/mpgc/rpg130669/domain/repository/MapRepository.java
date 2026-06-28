package it.unicam.cs.mpgc.rpg130669.domain.repository;

import it.unicam.cs.mpgc.rpg130669.domain.model.map.GameMap;

import java.util.List;
import java.util.Optional;

/**
 * Handles the output port for maps.
 * Implemented in the infrastructure.persistence.xml layer.
 * ----
 * Optional is used to handle failed loading scenarios and avoid NullPointerExceptions.
 */
public interface MapRepository {
    Optional<GameMap> loadById(int levelId);
    List<GameMap> loadAll();
}
