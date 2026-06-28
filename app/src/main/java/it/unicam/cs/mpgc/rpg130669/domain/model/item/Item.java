package it.unicam.cs.mpgc.rpg130669.domain.model.item;

/**
 * Common contract for all inventory items.
 * Polymorphic (de)serialization is entirely delegated to
 * ItemTypeAdapter in the infrastructure layer — the domain remains agnostic
 * of any persistence details.
 */
public interface Item {
    String getId();
    String getName();
    String getDescription();
    boolean isBroken();
}
