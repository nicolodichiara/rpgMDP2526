package it.unicam.cs.mpgc.rpg130669.domain.model.player;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishBehaviorState;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Stat {
    PERCEPTION("perception"),
    CASTING("casting"),
    STRENGTH("strength"),
    PATIENCE("patience"),
    CRAFTING("crafting");
    private final String code;

    Stat(String code){
        this.code = code;
    }
    @JsonValue
    public String getCode(){return this.code;}

    private static final Map<String, Stat> BY_CODE = Arrays.stream(values())
            .collect(Collectors
                    .toUnmodifiableMap(Stat::getCode, t -> t));
    @JsonCreator
    public static Stat fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Stat non conosciuto : null");
        }
        Stat stat = BY_CODE.get(code);
        if (stat == null) throw new IllegalArgumentException("Stat non conosciuto :" + code);
        return stat;
    }

}
