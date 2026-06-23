package it.unicam.cs.mpgc.rpg130669.domain.model.item;

import java.util.Objects;

/**
 * Implementazione base dei campi comuni a tutti gli item.
 * La durabilità è gestita qui: quando scende a 0 l'item è rotto
 * e non può essere usato. -1 significa durabilità infinita (item non consumabili).
 */
public abstract class AbstractItem implements Item {

    private final String id;
    private final String name;
    private final String description;
    private int durability; // -1 = infinita

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
     * Riduce la durabilità di `amount`. No-op se durabilità infinita o già rotto.
     */
    public void wear(int amount) {
        if (isBroken()) return;
        if (amount <= 0) throw new IllegalArgumentException("amount deve essere > 0: " + amount);
        durability = Math.max(0, durability - amount);
    }

    /**
     * equals e hashCode basati su id — due item con lo stesso id
     * sono identici per Inventory (usa Item come chiave di mappa).
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
