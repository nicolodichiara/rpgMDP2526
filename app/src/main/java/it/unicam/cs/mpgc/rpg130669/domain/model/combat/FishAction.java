package it.unicam.cs.mpgc.rpg130669.domain.model.combat;

/**
 * Azioni che può fare il pesce, durante il FISH_TURN:
 * la scelta dell'azione è probabilistica.
 * 1. si dibatte — riduce rod.durability
 * 2. nuota lontano — aumenta la distanza virtuale
 * 3. si stanca — perde stamina autonomamente
 * 4. morde più forte — danno extra alla lenza
 */

public enum FishAction {
    STRUGGLE,
    SWIM_AWAY,
    TIRE,
    BITE_HARDER
}
