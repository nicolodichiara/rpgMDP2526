package it.unicam.cs.mpgc.rpg130669.domain.model.map;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GRASS        —     camminabile, non pescabile
 * DOCK         —     camminabile,     pescabile
 * WATER        — non camminabile,     pescabile
 * DEEP WATER   — non camminabile,     pescabile
 * ROCK         — non camminabile, non pescabile
 * SAND         —     camminabile, non pescabile
 */

public enum TileType {

    GRASS     ("grass",      true,  false),
    DOCK      ("dock",       true,  true),
    WATER     ("water",      false, true),
    DEEP_WATER("deep_water", false, true),
    ROCK      ("rock",       false, false),
    SAND      ("sand",       true,  false);

    private final String  code;
    private final boolean walkable;
    private final boolean fishable;

    TileType(String code, boolean walkable, boolean fishable) {
        if(code == null) throw new IllegalArgumentException("code non deve essere null");
        this.code = code;
        this.walkable = walkable;
        this.fishable = fishable;
    }

    private static final Map<String, TileType> BY_CODE =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(TileType::getCode, t -> t));

    public String  getCode()    { return code;     }
    public boolean isWalkable() { return walkable; }
    public boolean isFishable() { return fishable; }

    public static TileType fromCode(String code) {
        TileType type = BY_CODE.get(code);
        if (type == null)
            throw new IllegalArgumentException("tiletype non conosciuto");
        return type;
    }
}