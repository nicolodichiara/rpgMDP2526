package it.unicam.cs.mpgc.rpg130669.domain.model.fish;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * rarità del pesce:
 * il campo level permette il confronto cardinale fra le rarità
 */
public enum FishRarity {

    @SerializedName("common")    COMMON   ("common",    1),
    @SerializedName("uncommon")  UNCOMMON ("uncommon",  2),
    @SerializedName("rare")      RARE     ("rare",      3),
    @SerializedName("legendary") LEGENDARY("legendary", 4);

    private final String code;
    private final int    level;

    FishRarity(String code, int level) {
        this.code  = code;
        this.level = level;
    }

    private static final Map<String, FishRarity> BY_CODE =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(FishRarity::getCode, r -> r));

    public String getCode()  { return code;  }
    public int    getLevel() { return level; }

    public static FishRarity fromCode(String code) {
        FishRarity rarity = BY_CODE.get(code);
        if (rarity == null)
            throw new IllegalArgumentException("FishRarity sconosciuta: " + code);
        return rarity;
    }
}

