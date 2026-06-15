package it.unicam.cs.mpgc.rpg130669.domain.model.item;

/**
 * Canna da pesca. Slot principale dell'equipaggiamento.
 *
 * power  → efficacia di PULL durante il combattimento (contrapposto a fish.combatStrength)
 * range  → distanza massima di lancio in tile (Chebyshev)
 */
public class FishingRod extends AbstractItem {

    private final int power;
    private final int range;

    public FishingRod(String id, String name, String description,
                      int durability, int power, int range) {
        super(id, name, description, durability);
        if (power <= 0) throw new IllegalArgumentException("power deve essere > 0: " + power);
        if (range <= 0) throw new IllegalArgumentException("range deve essere > 0: " + range);
        this.power = power;
        this.range = range;
    }

    public int getPower() { return power; }
    public int getRange() { return range; }
}