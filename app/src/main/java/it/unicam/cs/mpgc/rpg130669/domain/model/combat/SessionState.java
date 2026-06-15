package it.unicam.cs.mpgc.rpg130669.domain.model.combat;

/**
 * sessione di gameplay del gioco:
 * 1. esca lanciata, in attesa di abboccata
 * 2. pesce entrato in range, può iniziare il combattimento
 * 3. turno del giocatore
 * 4. turno del pesce
 * 5. pesce catturato — sessione conclusa con successo
 * 6. pesce fuggito — sessione conclusa senza cattura
 * 7. giocatore ha abbandonato
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
