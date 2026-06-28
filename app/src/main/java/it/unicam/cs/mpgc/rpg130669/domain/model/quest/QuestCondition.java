package it.unicam.cs.mpgc.rpg130669.domain.model.quest;

import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.repository.JournalRepository;

/**
 * Verifiable condition on the game state. Each implementation
 * encapsulates a criterion, and a quest can combine multiple conditions using
 * a logical AND in Quest.isSatisfied().
 */
public interface QuestCondition {
    boolean isSatisfied(Player player, JournalRepository journal);
}
