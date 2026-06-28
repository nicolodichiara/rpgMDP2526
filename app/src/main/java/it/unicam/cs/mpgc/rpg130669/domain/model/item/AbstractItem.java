package it.unicam.cs.mpgc.rpg130669.domain.model.item;

import java.util.Objects;

/**
 * Base implementation of fields common to all items.
 * Durability is managed here: when it drops to 0, the item is broken
 * and cannot be used. -1 signifies infinite durability (non-consumable items).
 */
public abstract class AbstractItem implements Item {

    private final String id;
    private final String name;
    private final String description;
    private int durability; // -1 = infinity

    protected AbstractItem(String id, String name, String description, int durability) {
        this.id          = Objects.requireNonNull(id,          "id non può essere null");
        this.name        = Objects.requireNonNull(name,        "name non può essere null");
        this.description = Objects.requireNonNull(description, "description non può essere null");
        if (durability < -1)
            throw new IllegalArgumentException("durability non valida: " + durability);
        this.durability  = durability;
    }

    @Override public String getId()          { return id;          }
    @Override public String getName()        { return name;        }
    @Override public String getDescription() { return description; }
    public    int    getDurability()         { return durability;  }

    @Override
    public boolean isBroken() {
        return durability <= 0;
    }

    /**
     * Reduces durability by `amount`. No-op if durability is infinite or already broken.
     */
    public void wear(int amount) {
        if (isBroken()) return;
        if (amount <= 0) throw new IllegalArgumentException("amount deve essere > 0: " + amount);
        durability = Math.max(0, durability - amount);
    }

    /**
     * equals and hashCode based on id — two items with the same id
     * are identical for Inventory (uses Item as a map key).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractItem other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
