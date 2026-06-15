package it.unicam.cs.mpgc.rpg130669.domain.model.world;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Condizione meteorologica corrente.
 * accessibleDuringStorm indica se la mappa è raggiungibile in questa condizione.
 */
public enum Weather {

    CLEAR  ("clear",   true),
    CLOUDY ("cloudy",  true),
    RAIN   ("rain",    true),
    STORM  ("storm",   false);

    private final String  code;
    private final boolean mapAccessible;

    Weather(String code, boolean mapAccessible) {
        this.code          = code;
        this.mapAccessible = mapAccessible;
    }

    private static final Map<String, Weather> BY_CODE =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(Weather::getCode, w -> w));

    @JsonValue
    public String  getCode()          { return code;          }
    public boolean isMapAccessible()  { return mapAccessible; }

    @JsonCreator
    public static Weather fromCode(String code) {
        Weather w = BY_CODE.get(code);
        if (w == null) throw new IllegalArgumentException("Weather sconosciuto: " + code);
        return w;
    }
}