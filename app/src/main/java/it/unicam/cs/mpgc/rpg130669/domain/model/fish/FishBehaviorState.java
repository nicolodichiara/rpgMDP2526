package it.unicam.cs.mpgc.rpg130669.domain.model.fish;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum FishBehaviorState {
    IDLE("idle"),
    WANDERING("wandering"),
    NEUTRAL("neutral"),
    ATTRACTED("attracted"),
    SCARED("scared"),
    FLEEING("fleeing");

    private final String code;

    FishBehaviorState(String code){
        this.code = code;
    }

    public String getCode(){return this.code;}

    private static final Map<String, FishBehaviorState> BY_CODE = Arrays.stream(values())
            .collect(Collectors
                    .toUnmodifiableMap(FishBehaviorState::getCode, t -> t));

    public static FishBehaviorState fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("FishBehaviourState non conosciuto : null");
        }
        FishBehaviorState state = BY_CODE.get(code);
        if (state == null) throw new IllegalArgumentException("FishBehaviourState non conosciuto :" + code);
        return state;
    }
}
