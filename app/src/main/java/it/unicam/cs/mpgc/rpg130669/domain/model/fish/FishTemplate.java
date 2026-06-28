package it.unicam.cs.mpgc.rpg130669.domain.model.fish;

import java.util.Objects;

/**
 * Static and immutable data of a fish species.
 * Loaded from XML at startup — never changes at runtime.
 *
 * perceptionThreshold: minimum player perception required to see the fish
 * as VISIBLE. Below this threshold, VisibilityService degrades it to SILHOUETTE / SHADOW / HIDDEN.
 */

public record FishTemplate(
        String id,
        String name,
        FishRarity rarity,
        BehaviorProfile behaviorProfile,
        int baseHp,
        int baseStamina,
        int combatStrength,
        int perceptionThreshold
) {
    public FishTemplate{
        Objects.requireNonNull(id, "id non può essere null");
        Objects.requireNonNull(name,            "name non può essere null");
        Objects.requireNonNull(rarity,          "rarity non può essere null");
        Objects.requireNonNull(behaviorProfile, "behaviorProfile non può essere null");

        if (baseHp              <= 0) throw new IllegalArgumentException("baseHp deve essere > 0");
        if (baseStamina         <= 0) throw new IllegalArgumentException("baseStamina deve essere > 0");
        if (combatStrength      <= 0) throw new IllegalArgumentException("combatStrength deve essere > 0");
        if (perceptionThreshold <  0) throw new IllegalArgumentException("perceptionThreshold non può essere negativo");
    }
}
