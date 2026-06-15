package it.unicam.cs.mpgc.rpg130669.domain.repository;

import it.unicam.cs.mpgc.rpg130669.domain.model.map.GameMap;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.world.WorldClock;

import java.util.Optional;

/**
 * Gestisce la porta di output per il salvataggio completo della partita
 * Implementata nel layer infrastracture.persistence.JSON
 * ----
 * Optional usato per gestire il mancato caricamento e quindi le NullPointerException
 */
public interface SaveGameRepository {
    void save(Player player, GameMap currentMap, WorldClock clock);
    Optional<SaveGameSnapshot> load(String playerId);
    boolean hasSave(String playerId);
    void    deleteSave(String playerId);

    /**
     * Snapshot immutabile dello stato di gioco caricato.
     * Usato dal GameSessionUseCase per ricostruire la sessione.
     */
    record SaveGameSnapshot(Player player, GameMap currentMap, WorldClock clock) {}
}
