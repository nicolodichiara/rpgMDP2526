package it.unicam.cs.mpgc.rpg130669.domain.model.quest;

import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Stat;
import it.unicam.cs.mpgc.rpg130669.domain.repository.JournalRepository;

import java.util.Objects;

/** Condition: reach a minimum value in a specific stat. */
public class ReachLevelCondition implements QuestCondition {

    private final Stat stat;
    private final int  required;

    public ReachLevelCondition(Stat stat, int required) {
        this.stat     = Objects.requireNonNull(stat, "stat non può essere null");
        if (required <= 0) throw new IllegalArgumentException("required deve essere > 0");
        this.required = required;
    }

    @Override
    public boolean isSatisfied(Player player, JournalRepository journal) {
        return player.getStat(stat) >= required;
    }
}
