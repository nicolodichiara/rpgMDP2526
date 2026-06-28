package it.unicam.cs.mpgc.rpg130669.domain.service;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishEntity;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishVisibility;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Stat;

/**
 * Calculates fish visibility for the player.
 * Mechanics: perceptionThreshold - player.PERCEPTION --> delta
 * delta <= 0  → VISIBLE
 * delta <= 2  → SILHOUETTE
 * delta <= 5  → SHADOW
 * delta > 5   → HIDDEN
 *
 * The underlying logic is:
 * - Separated from the entities themselves as it depends solely on two distinct objects:
 * Player and FishEntity.
 * - Stateless, meaning the methods are pure.
 */

public class VisibilityService {
    public FishVisibility compute(FishEntity fish, Player player) {
        int delta = fish.getTemplate().perceptionThreshold()
                - player.getStat(Stat.PERCEPTION);

        if (delta <= 0) return FishVisibility.CLEAR;
        if (delta <= 2) return FishVisibility.SILHOUETTE;
        if (delta <= 5) return FishVisibility.SHADOW;
        return FishVisibility.HIDDEN;
    }}
