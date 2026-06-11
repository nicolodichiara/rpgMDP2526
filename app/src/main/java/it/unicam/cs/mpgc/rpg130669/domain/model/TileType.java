package it.unicam.cs.mpgc.rpg130669.domain.model;


import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum TileType {
    GRASS("grass", true, false),
    DOCK("dock", true, true),
    WATER("water", false, true),
    DEEP_WATER("deep_water", false, true),
    ROCK("rock", false, false),
    SAND("sand", true, false);

// deseriallizzazione della stringa, creo un campo code, walkable e fishable

    private final String code;
    private final boolean fishable;
    private final boolean walkable;

    TileType(String code, boolean walkable, boolean fishable) {
        this.code = code;
        this.walkable = walkable;
        this.fishable = fishable;
    }

    public String getCode() { return code;}

    public boolean isFishable() { return fishable;}

    public boolean isWalkable() { return walkable;}

    // mappa statica che permette O(1), costruita al caricamento della classe
    private static final Map<String, TileType> BY_CODE = Arrays.stream(values())
            .collect(Collectors
                    .toUnmodifiableMap(TileType::getCode, t -> t));

    public static TileType fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("tiletype non conosciuto : null");
        }
        TileType type = BY_CODE.get(code);
        if (type == null) throw new IllegalArgumentException("tiletype non conosciuto :" + code);
        return type;
        }

    }