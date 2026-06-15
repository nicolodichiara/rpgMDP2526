package it.unicam.cs.mpgc.rpg130669.domain.model.world;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Collectors;
import java.util.*;

public enum TimeOfDay {
    DAWN     ("dawn",      0),
    MORNING  ("morning",   1),
    NOON     ("noon",      2),
    AFTERNOON("afternoon", 3),
    DUSK     ("dusk",      4),
    NIGHT    ("night",     5);

    private final String code;
    private final int order;

    TimeOfDay(String code, int value){
        this.code = code;
        this.order = value;
    }
    private static final Map<String, TimeOfDay> BY_CODE =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(TimeOfDay::getCode, t -> t));


    @JsonValue
    public String getCode()  { return code;  }

    public int    getOrder() { return order; }


    // presente qui la successione, in modo da lasciare sconnesso WorldClock e
    // renderlo migliore a livello di dipendenze

    public TimeOfDay next() {
        TimeOfDay[] values = values();
        return values[(order + 1) % values.length];
    }

    @JsonCreator
    public static TimeOfDay fromCode(String code) {
        TimeOfDay t = BY_CODE.get(code);
        if (t == null) throw new IllegalArgumentException("TimeOfDay sconosciuto: " + code);
        return t;
    }
}
