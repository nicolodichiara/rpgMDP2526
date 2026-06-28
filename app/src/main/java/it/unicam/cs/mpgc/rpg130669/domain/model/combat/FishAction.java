package it.unicam.cs.mpgc.rpg130669.domain.model.combat;

/**
 * Actions the fish can perform during the FISH_TURN:
 * action selection is probabilistic.
 * 1. STRUGGLE          — reduces rod.durability
 * 2. SWIM AWAY         — increases virtual distance
 * 3. TIRE              — autonomously loses stamina
 * 4. BITE HARDER       — extra damage to the line
 */

public enum FishAction {
    STRUGGLE,
    SWIM_AWAY,
    TIRE,
    BITE_HARDER
}
