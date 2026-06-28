package it.unicam.cs.mpgc.rpg130669.domain.model.player;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Skill-based player statistics.
 * Each stat grows by actively using that specific stat — there is no global XP.
 *
 * PERCEPTION  — expands FishVisibility, increases perceived reactionRange
 * CASTING     — casting distance and accuracy
 * STRENGTH    — PULL effectiveness during combat
 * PATIENCE    — reduces fish escape probability during WAIT
 * CRAFTING    — unlocks recipes and crafted item quality (V2)
 */

public enum Stat {

    @SerializedName("perception") PERCEPTION("perception"),
    @SerializedName("casting")    CASTING   ("casting"),
    @SerializedName("strength")   STRENGTH  ("strength"),
    @SerializedName("patience")   PATIENCE  ("patience"),
    @SerializedName("crafting")   CRAFTING  ("crafting");

    private final String code;

    Stat(String code) { this.code = code; }

    private static final Map<String, Stat> BY_CODE =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(Stat::getCode, s -> s));

    public String getCode() { return code; }

    public static Stat fromCode(String code) {
        Stat s = BY_CODE.get(code);
        if (s == null) throw new IllegalArgumentException("Stat sconosciuta: " + code);
        return s;
    }
}
