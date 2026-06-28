package it.unicam.cs.mpgc.rpg130669.domain.model.item;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishRarity;

/**
 * Bait. Consumable that can be used during a FishingSession (USE_BAIT action).
 *
 * attractionBonus  → modifies the transition probability from NEUTRAL to ATTRACTED
 * targetRarity     → if not null, the bonus applies only to fish of that specific rarity
 * (e.g., special bait for LEGENDARY fish)
 */
public class Bait extends AbstractItem {

    private final float      attractionBonus;
    private final FishRarity targetRarity;     // null = funziona su tutti

    public Bait(String id, String name, String description,
                int durability, float attractionBonus, FishRarity targetRarity) {
        super(id, name, description, durability);
        if (attractionBonus <= 0f)
            throw new IllegalArgumentException("attractionBonus deve essere > 0: " + attractionBonus);
        this.attractionBonus = attractionBonus;
        this.targetRarity    = targetRarity;
    }

    public float      getAttractionBonus() { return attractionBonus; }
    public FishRarity getTargetRarity()    { return targetRarity;    }
    public boolean    isUniversal()        { return targetRarity == null; }

    /**
     * Returns the effective bonus for a fish of a given rarity.
     * If the bait is specific and the rarity does not match, the bonus is 0.
     */
    public float effectiveBonus(FishRarity fishRarity) {
        if (isUniversal() || targetRarity == fishRarity) return attractionBonus;
        return 0f;
    }
}