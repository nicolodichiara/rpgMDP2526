package it.unicam.cs.mpgc.rpg130669.domain.model.world;

import com.google.gson.annotations.SerializedName;

/**
 * Determines the weather cycle and map accessibility.
 * CLEAR    —     accessible
 * CLOUDY   —     accessible
 * RAIN     —     accessible
 * STORM    — non-accessible
 */

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Weather {

    @SerializedName("clear")  CLEAR ("clear",  true),
    @SerializedName("cloudy") CLOUDY("cloudy", true),
    @SerializedName("rain")   RAIN  ("rain",   true),
    @SerializedName("storm")  STORM ("storm",  false);

    private final String  code;
    private final boolean mapAccessible;

    Weather(String code, boolean mapAccessible) {
        this.code          = code;
        this.mapAccessible = mapAccessible;
    }

    private static final Map<String, Weather> BY_CODE =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(Weather::getCode, w -> w));

    public String  getCode()         { return code;          }
    public boolean isMapAccessible() { return mapAccessible; }

    public static Weather fromCode(String code) {
        Weather w = BY_CODE.get(code);
        if (w == null) throw new IllegalArgumentException("Weather sconosciuto: " + code);
        return w;
    }
}