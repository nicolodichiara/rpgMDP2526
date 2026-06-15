package it.unicam.cs.mpgc.rpg130669.domain.model.item;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishRarity;

/**
 * Esca. Consumabile usabile durante FishingSession (azione USE_BAIT).
 *
 * attractionBonus  → modifica la probabilità di transizione NEUTRAL→ATTRACTED
 * targetRarity     → se non null, il bonus vale solo per pesci di quella rarità
 *                    (es. esca speciale per pesci LEGENDARY)
 */
public class Bait extends AbstractItem {

    private final float      attractionBonus;
    private final FishRarity targetRarity;     // null = funziona su tutti

    public Bait(String id, String name, String description,
                int durability, float attractionBonus, FishRarity targetRarity) {
        super(id, name, description, durability);
        if (attractionBonus <= 0f)
            throw new IllegalArgumentException("attractionBonus deve essere > 0: " + attractionBonus);
        this.attractionBonus = attractionBonus;
        this.targetRarity    = targetRarity;
    }

    public float      getAttractionBonus() { return attractionBonus; }
    public FishRarity getTargetRarity()    { return targetRarity;    }
    public boolean    isUniversal()        { return targetRarity == null; }

    /**
     * Ritorna il bonus effettivo per un pesce di una certa rarità.
     * Se l'esca è specifica e la rarità non corrisponde, il bonus è 0.
     */
    public float effectiveBonus(FishRarity fishRarity) {
        if (isUniversal() || targetRarity == fishRarity) return attractionBonus;
        return 0f;
    }
}