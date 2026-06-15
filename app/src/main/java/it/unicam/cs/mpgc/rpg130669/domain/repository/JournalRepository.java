package it.unicam.cs.mpgc.rpg130669.domain.repository;

import java.util.List;
import java.util.Optional;

/**
 * Gestisce la porta di output per il fishing journal
 * Implementata nel layer infrastracture.persistence.SQlite
 * ----
 * Optional usato per gestire il mancato caricamento e quindi le NullPointerException
 */
public interface JournalRepository {
    /** Registra una nuova cattura. */
    void recordCatch(String playerId, String fishId, int weight);

    /** Numero totale di catture per una specie. */
    int getCatchCount(String playerId, String fishId);

    /** Peso massimo catturato per una specie (record personale). */
    Optional<Integer> getRecord(String playerId, String fishId);

    /** Tutte le specie catturate almeno una volta da questo giocatore. */
    List<String> getDiscoveredFishIds(String playerId);

    /** True se il giocatore ha già catturato questa specie. */
    boolean hasDiscovered(String playerId, String fishId);
}
