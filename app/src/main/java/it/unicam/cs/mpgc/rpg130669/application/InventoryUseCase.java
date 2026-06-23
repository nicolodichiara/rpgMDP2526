package it.unicam.cs.mpgc.rpg130669.application;

import it.unicam.cs.mpgc.rpg130669.domain.model.item.Bait;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.FishingRod;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.Item;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;

import java.util.Objects;

/**
 * gestione dell'inventario:
 * valida le precondizioni, si occupa di togliere gli oggetti
 * una volta utilizzati o rotti.
 */

public class InventoryUseCase {

    public void addItem(Item item, Player player, int quantity){
        Objects.requireNonNull(item, "item non può essere null");
        player.getInventory().add(item, quantity);
    }

    public void removeItem(Item item, Player player, int quantity){
        if (!player.getInventory().contains(item)) throw new IllegalStateException("item non presente");
        player.getInventory().remove(item, quantity);
    }

    public Bait consumeBait(Player player, String baitId){
        Item item = findById(player, baitId);
        if (! (item instanceof Bait bait)) throw new IllegalArgumentException("item non è bait");
        if (bait.isBroken()) throw new IllegalStateException("l'esca è esaurita");
        player.getInventory().remove(bait, 1);
        return bait;
    }
    /**
     * Recupera la canna equipaggiata (primo FishingRod non rotta trovata).
     * In futuro: sostituito con uno slot equipaggiamento dedicato.
     */
    public FishingRod getEquippedRod(Player player) {
        return player.getInventory().getItemSet().stream()
                .filter(i -> i instanceof FishingRod rod && !rod.isBroken())
                .map(i -> (FishingRod) i)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nessuna canna disponibile"));
    }

    private Item findById(Player player, String baitId){
        return player.getInventory().getItemSet().stream()
                .filter(i -> i.getId().equals(baitId))
                .findFirst().orElseThrow(() -> new IllegalStateException("Item non trovato"));
    }
}
