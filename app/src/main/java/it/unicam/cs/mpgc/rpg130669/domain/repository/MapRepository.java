package it.unicam.cs.mpgc.rpg130669.domain.repository;

import it.unicam.cs.mpgc.rpg130669.domain.model.map.GameMap;

import java.util.List;
import java.util.Optional;

/**
 * Gestisce la porta di output per le mappe
 * Implementata nel layer infrastracture.persistence.xml
 * ----
 * Optional usato per gestire il mancato caricamento e quindi le NullPointerException()
 */
public interface MapRepository {
    Optional<GameMap> loadById(int levelId);
    List<GameMap> loadAll();
}
