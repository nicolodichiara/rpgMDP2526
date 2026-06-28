package it.unicam.cs.mpgc.rpg130669.presentation.viewmodel;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishBehaviorState;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishVisibility;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.TileType;

/**
 * Immutable data that the MapRenderer uses to render a single tile.
 * Decouples the renderer from the domain model.
 */
public record TileViewModel(
        TileType        tileType,
        boolean         hasPlayer,
        boolean         hasFish,
        FishVisibility  fishVisibility,
        FishBehaviorState fishState
) {
    /** Tile with no entity on top. */
    public static TileViewModel empty(TileType type) {
        return new TileViewModel(type, false, false, null, null);
    }
}
