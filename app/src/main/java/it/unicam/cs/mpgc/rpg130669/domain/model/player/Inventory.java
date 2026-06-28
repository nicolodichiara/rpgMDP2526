package it.unicam.cs.mpgc.rpg130669.domain.model.player;

import it.unicam.cs.mpgc.rpg130669.domain.model.item.Item;

import java.util.*;

/**
 * Player's inventory — maps an Item to its quantity.
 *
 * Uses Item as the map key: two items with the same id
 * are considered identical (equals/hashCode are delegated to the id in AbstractItem).
 *
 * Uses a LinkedHashMap to leverage its FIFO property (maintaining the insertion order of items).
 * Note: this implementation is not synchronized.
 */


public class Inventory {
    private final Map<Item, Integer> items;

    /**
     * Adds `quantity` units of the item.
     * If the item is already present, increments the existing quantity.
     */
    public Inventory(){
        this.items = new LinkedHashMap<>();
    }

    public void add(Item item, int quantity) {
        Objects.requireNonNull(item, "item non può essere null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity deve essere > 0: " + quantity);
        items.merge(item, quantity, Integer::sum);
    }
    /**
     * Removes `quantity` units of the item.
     * If the quantity drops to zero, the entry is removed from the map.
     *
     * @throws IllegalStateException if there are not enough units available
     */
    public void remove(Item item, int quantity) {

        Objects.requireNonNull(item, "item non può essere null");

        // check
        if (quantity <= 0) throw new IllegalArgumentException("quantity deve essere > 0: " + quantity);

        int current = items.get(item);


        if (quantity == current) items.remove(item);
        else if (quantity > current) throw new IllegalStateException("la quantità che si vuole rimuovere è superiore a quella presente nell'inventario");
        else items.put(item, current - quantity);
    }

    public boolean contains(Item item){ return items.containsKey(item); }
    public boolean isEmpty(){ return items.isEmpty(); }
    public int getQuantity(Item item){ return items.getOrDefault(item, 0); }

    public Map<Item, Integer> getItems(){
        return Collections.unmodifiableMap(items);
    }
    public Set<Item> getItemSet(){
        return Collections.unmodifiableSet(items.keySet());
    }

}

