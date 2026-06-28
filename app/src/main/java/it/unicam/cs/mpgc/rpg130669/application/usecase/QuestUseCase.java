package it.unicam.cs.mpgc.rpg130669.application.usecase;

import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.quest.Quest;
import it.unicam.cs.mpgc.rpg130669.domain.repository.JournalRepository;

import java.util.List;

/**
 * Verifies and updates the status of the quests.
 * Called by GameSessionUseCase after each significant action
 * (catch, level-up). Does not store quests in memory — receives them
 * from the presentation layer or repository (to be implemented in V2).
 */
public class QuestUseCase {

    private final JournalRepository journal;

    public QuestUseCase(JournalRepository journal) {
        this.journal = journal;
    }

    /**
     * Checks all uncompleted quests and marks them if satisfied.
     * @return list of quests newly completed during this check
     */
    public List<Quest> checkAndComplete(Player player, List<Quest> quests) {
        return quests.stream()
                .filter(q -> !q.isCompleted())
                .filter(q -> q.isSatisfied(player, journal))
                .peek(Quest::markCompleted)
                .toList();
    }

    public boolean isCompleted(Quest quest) {
        return quest.isCompleted();
    }
}
