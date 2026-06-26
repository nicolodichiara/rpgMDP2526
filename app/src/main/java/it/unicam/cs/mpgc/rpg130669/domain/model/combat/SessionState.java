package it.unicam.cs.mpgc.rpg130669.domain.model.combat;

/**
 * sessione di gameplay del gioco:
 *  CASTING      — esca lanciata, in attesa di abboccata
 *  FISH SPOTTED — pesce entrato in range, può iniziare il combattimento
 *  PLAYER TURN  — turno del giocatore
 *  FISH TURN    — turno del pesce
 *  CAUGHT       — sessione conclusa con successo
 *  ESCAPED      — sessione conclusa senza cattura
 *  GIVEN UP     — giocatore ha abbandonato
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
