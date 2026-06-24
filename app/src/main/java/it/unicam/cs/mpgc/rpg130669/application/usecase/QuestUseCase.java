package it.unicam.cs.mpgc.rpg130669.application.usecase;

import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.quest.Quest;
import it.unicam.cs.mpgc.rpg130669.domain.repository.JournalRepository;

import java.util.List;

/**
 * Verifica e aggiorna lo stato delle quest.
 * Chiamato da GameSessionUseCase dopo ogni azione significativa
 * (cattura, level-up). Non tiene in memoria le quest — le riceve
 * dalla presentazione o dal repository (da implementare in V2).
 */
public class QuestUseCase {

    private final JournalRepository journal;

    public QuestUseCase(JournalRepository journal) {
        this.journal = journal;
    }

    /**
     * Controlla tutte le quest non completate e le marca se soddisfatte.
     * @return lista delle quest appena completate in questo check
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
