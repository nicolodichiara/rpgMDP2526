package it.unicam.cs.mpgc.rpg130669.domain.model.fish;
/**
 * Profilo comportamentale immutabile di una specie di pesce.
 * Definisce i pesi probabilistici del passaggio tra uno stato e l'altro.
 *
 * curiosity      → probabilità [0,1] di avvicinarsi all'esca quando NEUTRAL
 * fearThreshold  → probabilità [0,1] di spaventarsi per un'azione del giocatore
 * wanderRate     → probabilità [0,1] di muoversi casualmente quando IDLE
 * reactionRange  → distanza (tile, Chebyshev) oltre cui il pesce ignora il giocatore
 * fleeSpeed      → tile percorse per turno quando FLEEING
 */
public record BehaviorProfile(
        float   curiosity,
        float   fearThreshold,
        float   wanderRate,
        int     reactionRange,
        int     fleeSpeed
) {

    public BehaviorProfile{
        if(curiosity < 0f || curiosity > 1f) throw new IllegalArgumentException("curiosity fuori [0,1]: " + curiosity);
        if(fearThreshold < 0f || fearThreshold > 1f) throw new IllegalArgumentException("fearThreshold fuori [0,1]: " + fearThreshold);
        if(wanderRate < 0f || wanderRate > 1f) throw new IllegalArgumentException("wanderRate fuori [0,1]: " + wanderRate);
        if(reactionRange <= 0 ) throw new IllegalArgumentException("reactionRange deve essere > 0" + reactionRange);
        if(fleeSpeed <= 0) throw new IllegalArgumentException("fleeSpeed deve essere > 0: " + fleeSpeed);
}}
