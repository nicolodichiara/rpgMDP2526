package it.unicam.cs.mpgc.rpg130669.domain.model.fish;
/**
 * Immutable behavioral profile of a fish species.
 * Defines the probabilistic weights of transitioning between states.
 * curiosity      → probability [0,1] of approaching the bait when NEUTRAL
 * fearThreshold  → probability [0,1] of scaring away due to a player action
 * wanderRate     → probability [0,1] of moving randomly when IDLE
 * reactionRange  → distance (tiles, Chebyshev) beyond which the fish ignores the player
 * fleeSpeed      → tiles traversed per turn when FLEEING
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
