package it.unicam.cs.mpgc.rpg130669.domain.model.quest;

import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.repository.JournalRepository;

import java.util.List;
import java.util.Objects;

/**
 * Gestione delle Quest, ancora non attivo nel gioco
 * Una quest è soddisfatta quando TUTTE le sue condizioni sono soddisfatte.
 * rewards è una lista di stringhe descrittive — il QuestUseCase
 * le interpreta e applica gli effetti concreti (sblocco mappa, item, ecc.).
 */
public class Quest {

    private final String id;
    private final String title;
    private final String description;
    private final List<QuestCondition> conditions;
    private final List<String> rewards;
    private boolean completed;

    public Quest(String id, String title, String description,
                 List<QuestCondition> conditions, List<String> rewards){
        this.id = Objects.requireNonNull(id, "id non può essere null");
        this.title = Objects.requireNonNull(title, "title non può essere null");
        this.description = Objects.requireNonNull(description, "description non può essere null");
        this.conditions = List.copyOf(conditions);
        this.rewards = List.copyOf(rewards);
        this.completed = false;
    }

    public boolean isSatisfied(Player player, JournalRepository journal){
        return conditions.stream().allMatch(c -> c.isSatisfied(player, journal));
    }


    // AI
    public String       getId()          { return id;          }
    public String       getTitle()       { return title;       }
    public String       getDescription() { return description; }
    public List<String> getRewards()     { return rewards;     }
    public boolean      isCompleted()    { return completed;   }
    public void         markCompleted()  { this.completed = true; }
}
