package it.unicam.cs.mpgc.rpg130669.presentation.viewmodel;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishBehaviorState;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishVisibility;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.TileType;

/**
 * Dato immutabile che il MapRenderer usa per disegnare una singola tile.
 * Disaccoppia il renderer dal domain model.
 */
public record TileViewModel(
        TileType        tileType,
        boolean         hasPlayer,
        boolean         hasFish,
        FishVisibility  fishVisibility,
        FishBehaviorState fishState
) {
    /** Tile senza entità sopra. */
    public static TileViewModel empty(TileType type) {
        return new TileViewModel(type, false, false, null, null);
    }
}
