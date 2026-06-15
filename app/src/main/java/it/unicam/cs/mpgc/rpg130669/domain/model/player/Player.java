package it.unicam.cs.mpgc.rpg130669.domain.model.player;
import  it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;

import java.util.*;

/*
    il player conserva:
    - position
    - progressione
    - stat
    - inventario

    la progresssione dei livelli funziona in modo indipendente per ogni skill.
    Il livello = media ponderata dei lv skill
 */



public class Player {
    private static final int STAT_BASE_VALUE = 1;
    private static final int STAT_MAX_VALUE = 10;
    private static final int EXP_VALUE_PER_LVL = 100;

    private final String id;
    private String name;
    private Position position;
    private final Inventory inventory;
    private final Map<Stat, Integer> stats;
    private final Map<Stat, Integer> expPerStat;

    public Player (String id, String name, Position initalPos){
        this.id = id;
        this.name = name;
        this.position = initalPos;

        this.inventory = new Inventory();

        this.stats = new EnumMap<>(Stat.class);
        this.expPerStat = new EnumMap<>(Stat.class);
        // inizializzazione delle mappe ai valori di base
        for (Stat i : Stat.values()){
            stats.put(i, STAT_BASE_VALUE);
            expPerStat.put(i, 0);
        }
    }

    // getter ai-generated

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

    // fine ai

    // livello generale del giocatore (media delle statistiche)

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

    /* aggiunta di exp a una determinata statistica

        3 casi:
        - 1) la skill == lv max, non aumento l'esperienza
        - 2) la exp + value > EXP_VALUE_PER_LVL aumento lv
        - 3) aumento l'exp

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

    public boolean checkMaxLvl(Stat stat) {
        return (stats.get(stat) >= STAT_MAX_VALUE);
    }


}

