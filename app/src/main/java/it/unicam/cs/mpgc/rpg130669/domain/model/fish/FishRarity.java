package it.unicam.cs.mpgc.rpg130669.domain.model.fish;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum FishRarity {
    COMMON("common", 1),
    RARE("rare", 2),
    VERY_RARE("very_rare", 3),
    EPIC("epic", 4),
    LEGENDARY("legendary", 5);

    private final String code;
    private final int level;

    FishRarity(String code, int level){
            this.code = code;
            this.level = level;
        }
        @JsonValue
        public String getCode(){ return this.code; }
        public int getLevel(){ return this.level; }

        private static final Map<String, FishRarity> BY_CODE = Arrays.stream(values())
                .collect(Collectors
                        .toUnmodifiableMap(FishRarity::getCode, t -> t));
        @JsonCreator
        public static FishRarity fromCode(String code) {
            if (code == null) {
                throw new IllegalArgumentException("FishRarity non conosciuto : null");
            }
            FishRarity rarity = BY_CODE.get(code);
            if (rarity == null) throw new IllegalArgumentException("FishRarity non conosciuto :" + code);
            return rarity;
        }
    }

