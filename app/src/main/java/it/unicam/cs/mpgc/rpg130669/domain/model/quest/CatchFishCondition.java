package it.unicam.cs.mpgc.rpg130669.domain.model.quest;

import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.repository.JournalRepository;

import java.util.Objects;

/**
 * funzione non implementata
 *
 * condizione per la cattura del pesce:
 *  Item
 *  LivelloGiocatore
 */

public class CatchFishCondition implements QuestCondition {
    private final String fishId;
    private final int    required;

    public CatchFishCondition(String fishId, int required){
        if (required <= 0) throw new IllegalArgumentException("required non può essere minore di 1");
        this.fishId = Objects.requireNonNull(fishId, "fishId non può essere null");
        this.required = required;
    }

    @Override
    public boolean isSatisfied(Player player, JournalRepository journalRepository){
        return journalRepository.getCatchCount(player.getId(), fishId) >= required;
    }
}
