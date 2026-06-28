package it.unicam.cs.mpgc.rpg130669.domain.model.player;
import  it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;

import java.util.*;

/**
 * The player maintains:
 * - position
 * - progression
 * - stats
 * - inventory
 * Level progression functions independently for each skill.
 * The overall level = weighted average of individual skill levels.
 */

public class Player {
    private static final int STAT_BASE_VALUE = 1;
    private static final int STAT_MAX_VALUE = 10;
    private static final int EXP_VALUE_PER_LVL = 100;

    private final String id;
    private final String name;
    private Position position;
    private final Inventory inventory;
    private final Map<Stat, Integer> stats;
    private final Map<Stat, Integer> expPerStat;

    public Player(String id, String name, Position initalPos) {
        this.id        = Objects.requireNonNull(id,       "id non può essere null");
        this.name      = Objects.requireNonNull(name,     "name non può essere null");
        this.position  = Objects.requireNonNull(initalPos, "posizione non può essere null");  // ← aggiunta
        this.inventory = new Inventory();
        this.stats     = new EnumMap<>(Stat.class);
        this.expPerStat = new EnumMap<>(Stat.class);
        for (Stat i : Stat.values()) {
            stats.put(i, STAT_BASE_VALUE);
            expPerStat.put(i, 0);
        }
    }

    /**
     * Restoration constructor used by SaveGameRepository to
     * recreate a Player from a saved state with exact values
     * (bypassing gainXp, which is incremental).
     * The inventory is populated separately by the repository.
     */
    public Player(String id, String name, Position position,
                  Map<Stat, Integer> stats, Map<Stat, Integer> xpPerStat) {
        this.id        = Objects.requireNonNull(id,       "id non può essere null");
        this.name      = Objects.requireNonNull(name,     "name non può essere null");
        this.position  = Objects.requireNonNull(position, "position non può essere null");
        this.inventory = new Inventory();

        this.stats     = new EnumMap<>(Stat.class);
        this.stats.putAll(Objects.requireNonNull(stats, "stats non può essere null"));

        this.expPerStat = new EnumMap<>(Stat.class);
        this.expPerStat.putAll(Objects.requireNonNull(xpPerStat, "xpPerStat non può essere null"));
    }

    /**
     * General level of the player (average of skill levels).
     */
    public int getLevel(){
        return (int) stats.values()
                .stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(STAT_BASE_VALUE);
    }

    public void setPosition (Position pos){
        this.position = Objects.requireNonNull(pos, "posizione non deve essere null");
    }

    /**
     * Adds XP to a specific skill.
     * Handles 3 cases:
     * - 1) skill is at max level: no experience is added.
     * - 2) exp + value >= EXP_VALUE_PER_LVL: increments the level.
     * - 3) standard case: increments the experience.
     */

    public void gainExp(Stat stat, int value) {
        Objects.requireNonNull(stat, "la stat non può essere nulla");
        if (value <= 0) throw new IllegalArgumentException("value " + value + " non è un valore valido");

        if (checkMaxLvl(stat)) {
            expPerStat.put(stat, 0);
            return;
        }

        int totalExp = expPerStat.getOrDefault(stat, 0) + value;

        int remainingExp = levelUp(stat, totalExp);

        expPerStat.put(stat, remainingExp);
    }

    public boolean canAccessMap(int requiredLevel) {
        return getLevel() >= requiredLevel;
    }

    public int levelUp(Stat stat, int currentExp) {
        while (currentExp >= EXP_VALUE_PER_LVL) {

            currentExp -= EXP_VALUE_PER_LVL;

            stats.put(stat, stats.get(stat) + 1);

            if (checkMaxLvl(stat)) {
                currentExp = 0;
                break;
            }
        }
        return currentExp;
    }

    /**
     * Returns the experience progress of a specific skill as a percentage.
     */
    public double getStatProgress(Stat stat){
        if (checkMaxLvl(stat)) return 1.0;
            else return (double) getXp(stat) / EXP_VALUE_PER_LVL;
    }
    public boolean checkMaxLvl(Stat stat) {
        return (stats.get(stat) >= STAT_MAX_VALUE);
    }

    // getter AI-generated

    public String    getId()        { return id;        }
    public String    getName()      { return name;      }
    public Position  getPosition()  { return position;  }
    public Inventory getInventory() { return inventory; }

    public int getStat(Stat stat) {
        return stats.get(Objects.requireNonNull(stat));
    }

    public int getXp(Stat stat) {
        return expPerStat.get(Objects.requireNonNull(stat));
    }

    public Map<Stat, Integer> getAllStats() {
        return Collections.unmodifiableMap(stats);
    }

}

