package it.unicam.cs.mpgc.rpg130669.domain.model.item;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Contratto comune per tutti gli item dell'inventario.
 * @JsonTypeInfo istruisce Jackson a includere il campo "itemType" nel JSON,
 * permettendo la deserializzazione polimorfica senza reflection manuale.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "itemType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FishingRod.class, name = "rod"),
        @JsonSubTypes.Type(value = Bait.class,       name = "bait"),
        @JsonSubTypes.Type(value = Accessory.class,  name = "accessory")
})
public interface Item {
    String getId();
    String getName();
    String getDescription();
    boolean isBroken();
}
