package it.unicam.cs.mpgc.rpg130669.infrastructure.persistence.json;

import com.google.gson.*;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.Item;

import java.lang.reflect.Type;

/**
 * Adapter Gson per la (de)serializzazione polimorfica di Item.
 *
 * Il JSON include un campo "itemType" con il nome semplice della classe
 * concreta (es. "FishingRod"). In deserializzazione il nome viene
 * ricongiunto al package del domain model item e risolto via
 * reflection (Class.forName) — la stessa tecnica discussa per evitare
 * un blocco if/else per ogni sottotipo di Item.
 *
 * Aggiungere un nuovo tipo (es. Lure) non richiede ALCUNA modifica
 * a questa classe: basta che implementi Item e stia nello stesso package.
 */
public class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {

    private static final String TYPE_FIELD   = "itemType";
    private static final String ITEM_PACKAGE = "it.unicam.cs.mpgc.rpg130669.domain.model.item.";

    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
        // Serializza con la classe CONCRETA — altrimenti perderesti i campi specifici
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
