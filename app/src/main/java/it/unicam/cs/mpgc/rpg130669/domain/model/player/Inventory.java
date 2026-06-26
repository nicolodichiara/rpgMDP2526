package it.unicam.cs.mpgc.rpg130669.domain.model.player;

import it.unicam.cs.mpgc.rpg130669.domain.model.item.Item;

import java.util.*;

/**
 * Inventario del giocatore — mappa Item → quantità.
 *
 * Usa Item come chiave della mappa: due item con lo stesso id
 * sono considerati identici (equals/hashCode delegati all'id in AbstractItem).
 *
 * Utilizzo della LinkedHashMap, usata per sfruttare la proprietà FIFO (ordine di raccolta degli oggetti)
 * Nota: implementazione non sincrona
 */


public class Inventory {
    private final Map<Item, Integer> items;

    /**
     * Aggiunge `quantity` unità dell'item.
     * Se l'item è già presente, somma alla quantità esistente.
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
     * Rimuove `quantity` unità dell'item.
     * Se la quantità scende a zero rimuove la entry dalla mappa.
     *
     * @throws IllegalStateException se non ci sono abbastanza unità
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

