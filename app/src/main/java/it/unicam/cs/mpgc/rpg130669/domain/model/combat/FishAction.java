package it.unicam.cs.mpgc.rpg130669.domain.model.combat;

/**
 * Azioni che può fare il pesce, durante il FISH_TURN:
 * la scelta dell'azione è probabilistica.
 * 1. STRUGGLE          — riduce rod.durability
 * 2. SWIM AWAY         — aumenta la distanza virtuale
 * 3. TIRE              — perde stamina autonomamente
 * 4. BITE HARDER       — danno extra alla lenza
 */

public enum FishAction {
    STRUGGLE,
    SWIM_AWAY,
    TIRE,
    BITE_HARDER
}
