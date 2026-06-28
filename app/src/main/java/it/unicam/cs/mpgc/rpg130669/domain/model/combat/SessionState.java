package it.unicam.cs.mpgc.rpg130669.domain.model.combat;

/**
 * Game session states:
 * CASTING      — bait cast, waiting for a bite
 * FISH SPOTTED — fish entered range, combat can begin
 * PLAYER TURN  — player's turn
 * FISH TURN    — fish's turn
 * CAUGHT       — session concluded successfully
 * ESCAPED      — session concluded without a catch
 * GIVEN UP     — player abandoned the session
 */

public enum SessionState {
    CASTING,
    FISH_SPOTTED,
    PLAYER_TURN,
    FISH_TURN,
    CAUGHT,
    ESCAPED,
    GIVEN_UP
}
