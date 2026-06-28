package it.unicam.cs.mpgc.rpg130669.domain.model.combat;
/**
 * Actions available during the player's turn:
 * 1. PULL          — uses rod.power vs fish.combatStrength
 * 2. USE BAIT      — applies ItemEffect to the CombatState
 * 3. WAIT          — the fish loses stamina passively
 * 4. GIVE UP       — abandons the session
 */
public enum PlayerAction {
    PULL,
    USE_BAIT,
    WAIT,
    GIVE_UP
}
