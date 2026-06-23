package it.unicam.cs.mpgc.rpg130669.domain.model.item;

/**
 * Contratto comune per tutti gli item dell'inventario.
 * La (de)serializzazione polimorfica è interamente delegata a
 * ItemTypeAdapter nel layer infrastructure — il domain non conosce
 * alcun dettaglio di persistenza.
 */
public interface Item {
    String getId();
    String getName();
    String getDescription();
    boolean isBroken();
}
