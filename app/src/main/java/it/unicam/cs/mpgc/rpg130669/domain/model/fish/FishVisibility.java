package it.unicam.cs.mpgc.rpg130669.domain.model.fish;

/**
 * Visibility of the fish on the map:
 * 1. CLEAR        — perfectly visible on the map
 * 2. SILHOUETTE   — different shape and color
 * 3. SHADOW       — darkened silhouette of the fish
 * 4. HIDDEN       — not visible on the map
 */

public enum FishVisibility {
    CLEAR,
    SILHOUETTE,
    SHADOW,
    HIDDEN
}
