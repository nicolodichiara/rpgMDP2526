package it.unicam.cs.mpgc.rpg130669.infrastructure.persistence.json;

import com.google.gson.*;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.Item;

import java.lang.reflect.Type;

/**
 * Gson adapter for polymorphic (de)serialization of Item.
 *
 * The JSON includes an "itemType" field containing the simple name of the
 * concrete class (e.g., "FishingRod"). During deserialization, this name is
 * recombined with the item domain model package and resolved via
 * reflection (Class.forName) — the same technique discussed to avoid
 * an if/else block for each Item subtype.
 *
 * Adding a new type (e.g., Lure) requires NO changes to this class:
 * it only needs to implement Item and reside in the same package.
 */
public class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {

    private static final String TYPE_FIELD   = "itemType";
    private static final String ITEM_PACKAGE = "it.unicam.cs.mpgc.rpg130669.domain.model.item.";

    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
    // Serialize with the CONCRETE class — otherwise specific fields would be lost
        JsonObject json = context.serialize(src, src.getClass()).getAsJsonObject();
        json.addProperty(TYPE_FIELD, src.getClass().getSimpleName());
        return json;
    }

    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String simpleClassName = obj.get(TYPE_FIELD).getAsString();

        try {
            Class<?> clazz = Class.forName(ITEM_PACKAGE + simpleClassName);
            return context.deserialize(obj, clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Tipo di Item sconosciuto: " + simpleClassName, e);
        }
    }
}
