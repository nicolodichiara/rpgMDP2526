package it.unicam.cs.mpgc.rpg130669.domain.model.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Mutable state of the current combat.
 * Separated from FishingSession to respect SRP:
 * FishingSession manages turn flow,
 * CombatState maintains combat numbers.
 *
 * virtualDistance  → virtual fish-line distance (increases with SWIM_AWAY)
 * rodDurability    → current durability of the rod (reduced by STRUGGLE/BITE_HARDER)
 * activeEffects    → textual descriptions of active effects (used by the UI)
 */
public class CombatState {

    private static final int MAX_DISTANCE = 10;

    private int virtualDistance;
    private int rodDurability;
    private int turnCount;
    private final List<String> activeEffects;

    public CombatState(int initialRodDurability){
        if (initialRodDurability <= 0)
            throw new IllegalArgumentException("initialRodDurability non può essere minore uguale a 0");
        this.virtualDistance = 0;
        this.turnCount = 0;
        this.activeEffects = new ArrayList<>();
        this.rodDurability = initialRodDurability;
    }

    public int getVirtualDistance()          {return virtualDistance;}
    public int getRodDurability()            {return rodDurability;}
    public int getTurnCount()                {return turnCount;}
    public List<String> getActiveEffects()   {return Collections.unmodifiableList(activeEffects);}

    public void increaseDistance(int amount)        {virtualDistance = Math.min(MAX_DISTANCE, virtualDistance + amount);}
    public void decreaseDistance(int amount)        {virtualDistance = Math.max(0, virtualDistance - amount);}
    public void damageRod(int amount)               {rodDurability = Math.max(0, rodDurability - amount);}
    public void addEffect(String effectDescription) {activeEffects.add(effectDescription);}
    public void clearEffects()                      {activeEffects.clear();}
    public void incrementTurn()                     {turnCount++;}

    public boolean isRodBroken()                    { return rodDurability   <= 0; }
    public boolean isFishTooFar()                   { return virtualDistance >= MAX_DISTANCE; }
}


