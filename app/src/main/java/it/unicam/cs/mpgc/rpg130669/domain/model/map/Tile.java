package it.unicam.cs.mpgc.rpg130669.domain.model.map;

import java.util.Objects;

//singola cella della mappa, delega i comportamenti a TileType

public record Tile(TileType type) {
    public Tile {
        Objects.requireNonNull(type, "tiletype non può essere null");
    }
    public boolean isWalkable(){return type.isWalkable();}
    public boolean isFishable(){return type.isFishable();}
}
