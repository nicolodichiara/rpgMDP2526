package it.unicam.cs.mpgc.rpg130669.domain.model.combat;
/**
 * azioni disponibili al turno del player:
 * 1. PULL          — usa rod.power vs fish.combatStrength
 * 2. USE BAIT      — applica ItemEffect al CombatState
 * 3. WAIT          — il pesce perde stamina passivamente
 * 4. GIVE UP       — abbandona la sessione
 */
public enum PlayerAction {
    PULL,
    USE_BAIT,
    WAIT,
    GIVE_UP
}
