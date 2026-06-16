package it.unicam.cs.mpgc.rpg130669.domain.model.quest;

import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.repository.JournalRepository;

/**
 * Condizione verificabile sullo stato di gioco, ogni implementazione
 * incapsula un criterio, una quest può combinare più condizioni usando
 * AND logico in Quest.isSatisfied().
 */
public interface QuestCondition {
    boolean isSatisfied(Player player, JournalRepository journal);
}
