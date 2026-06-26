package it.unicam.cs.mpgc.rpg130669.domain.model.world;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tempi della giornata che si alternano ciclicamente
 * DAWN
 * MORNING
 * NOON
 * AFTERNOON
 * DUSK
 * NIGHT
 */

public enum TimeOfDay {

    @SerializedName("dawn")      DAWN     ("dawn",      0),
    @SerializedName("morning")   MORNING  ("morning",   1),
    @SerializedName("noon")      NOON     ("noon",      2),
    @SerializedName("afternoon") AFTERNOON("afternoon", 3),
    @SerializedName("dusk")      DUSK     ("dusk",      4),
    @SerializedName("night")     NIGHT    ("night",     5);

    private final String code;
    private final int    order;

    TimeOfDay(String code, int order) {
        this.code  = code;
        this.order = order;
    }

    private static final Map<String, TimeOfDay> BY_CODE =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(TimeOfDay::getCode, t -> t));

    public String getCode()  { return code;  }
    public int    getOrder() { return order; }

    public TimeOfDay next() {
        TimeOfDay[] values = values();
        return values[(order + 1) % values.length];
    }

    public static TimeOfDay fromCode(String code) {
        TimeOfDay t = BY_CODE.get(code);
        if (t == null) throw new IllegalArgumentException("TimeOfDay sconosciuto: " + code);
        return t;
    }
}
