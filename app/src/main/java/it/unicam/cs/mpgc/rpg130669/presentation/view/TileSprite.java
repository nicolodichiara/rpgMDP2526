package it.unicam.cs.mpgc.rpg130669.presentation.view;

/**
 * Maps TileType -> rendering color.
 * In V2, replace getColor() with getImage(), which loads
 * a tile from a PNG tilesheet — no other classes will change.
 */

import it.unicam.cs.mpgc.rpg130669.domain.model.map.TileType;
import javafx.scene.paint.Color;

public enum TileSprite {

    GRASS     (TileType.GRASS,      Color.web("#4a7c3f")),
    DOCK      (TileType.DOCK,       Color.web("#8B6914")),
    WATER     (TileType.WATER,      Color.web("#2a6fa8")),
    DEEP_WATER(TileType.DEEP_WATER, Color.web("#1a4a7a")),
    ROCK      (TileType.ROCK,       Color.web("#5a5a5a")),
    SAND      (TileType.SAND,       Color.web("#c8a84b"));

    private final TileType tileType;
    private final Color    color;

    TileSprite(TileType tileType, Color color) {
        this.tileType = tileType;
        this.color    = color;
    }

    public Color getColor() { return color; }

    public static TileSprite of(TileType type) {
        for (TileSprite s : values())
            if (s.tileType == type) return s;
        return GRASS; // fallback
    }

}
