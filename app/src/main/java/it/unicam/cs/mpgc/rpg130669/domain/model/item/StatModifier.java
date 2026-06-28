package it.unicam.cs.mpgc.rpg130669.domain.model.item;

import it.unicam.cs.mpgc.rpg130669.domain.model.player.Stat;

import java.util.Objects;

/**
 * Modifier for a player stat applied by an Accessory.
 * amount can be negative (malus) or positive (bonus).
 */
public record StatModifier(Stat stat, int amount) {

    public StatModifier {
        Objects.requireNonNull(stat, "stat non può essere null");
        if (amount == 0)
            throw new IllegalArgumentException("amount == 0 non ha effetto");
    }

    public boolean isBonus() { return amount > 0; }
    public boolean isMalus() { return amount < 0; }
}