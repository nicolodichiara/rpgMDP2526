package it.unicam.cs.mpgc.rpg130669.domain.model.combat;
/**
 * azioni disponibili al turno del player:
 * 1. tira la lenza — usa rod.power vs fish.combatStrength
 * 2. usa un item dall'inventario — applica ItemEffect al CombatState
 * 3. aspetta — il pesce perde stamina passivamente
 * 4. abbandona la sessione
 */
public enum PlayerAction {
    PULL,
    USE_BAIT,
    WAIT,
    GIVE_UP
}
