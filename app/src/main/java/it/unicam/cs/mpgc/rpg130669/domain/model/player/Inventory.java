package it.unicam.cs.mpgc.rpg130669.domain.model.player;

import it.unicam.cs.mpgc.rpg130669.domain.model.item.Item;

import java.util.*;

public class Inventory {
    private final Map<Item, Integer> items;

    // utilizzo della LinkedHashMap, usata per sfruttare la proprietà FIFO (ordine di raccolta degli oggetti)
    // Note that this implementation is not synchronized
    public Inventory(){
        this.items = new LinkedHashMap<>();
    }

    public void add(Item item, int quantity) {
        Objects.requireNonNull(item, "item non può essere null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity deve essere > 0: " + quantity);
        items.merge(item, quantity, Integer::sum);
    }

    public void remove(Item item, int quantity) {

        Objects.requireNonNull(item, "item non può essere null");

        // check
        if (quantity <= 0) throw new IllegalArgumentException("quantity deve essere > 0: " + quantity);

        int current = getQuantity(item);


        if (quantity == current) items.remove(item);
        if (quantity > current) items.put(item, 0);
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

