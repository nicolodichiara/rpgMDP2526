package it.unicam.cs.mpgc.rpg130669.domain.repository;

import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;

import java.util.Optional;

/**
 * Gestisce la porta di output per i dati del giocatore
 * Implementata nel layer infrastracture.persistence.json
 * ----
 * Optional usato per gestire il mancato caricamento e quindi le NullPointerException()
 */
public interface CharacterRepository {
    void   save(Player player);
    Optional<Player> load(String playerId);
    boolean exists(String playerId);
    void   delete(String playerId);
}
