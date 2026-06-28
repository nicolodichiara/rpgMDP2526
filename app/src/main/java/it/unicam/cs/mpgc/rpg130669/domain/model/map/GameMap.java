package it.unicam.cs.mpgc.rpg130669.domain.model.map;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Map of a game level.
 * Composes a TileGrid (physical structure) and SpawnZone[] (spawn logic).
 * Maintains the list of FishEntity instances currently alive on the map.
 *
 * requiredLevel is the minimum player level required for access.
 */
public class GameMap {

    private final int             levelId;
    private final String          name;
    private final TileGrid        grid;
    private final List<SpawnZone> spawnZones;
    private final int             requiredLevel;
    private final List<FishEntity> activeFish;

    public GameMap(int levelId, String name, TileGrid grid,
                   List<SpawnZone> spawnZones, int requiredLevel) {
        if (levelId <= 0) throw new IllegalArgumentException("levelId deve essere > 0");
        this.levelId       = levelId;
        this.name          = Objects.requireNonNull(name, "name non può essere null");
        this.grid          = Objects.requireNonNull(grid, "grid non può essere null");
        this.spawnZones    = List.copyOf(spawnZones);
        if (requiredLevel < 0) throw new IllegalArgumentException("requiredLevel non può essere negativo");
        this.requiredLevel = requiredLevel;
        this.activeFish    = new ArrayList<>();
    }

    public int             getLevelId()       { return levelId;       }
    public String          getName()          { return name;          }
    public TileGrid        getGrid()          { return grid;          }
    public List<SpawnZone> getSpawnZones()    { return spawnZones;    }
    public int             getRequiredLevel() { return requiredLevel; }

    public List<FishEntity> getActiveFish() {
        return Collections.unmodifiableList(activeFish);
    }

    public void addFish(FishEntity fish) {
        Objects.requireNonNull(fish, "fish non può essere null");
        activeFish.add(fish);
    }

    /** First walkable tile of the grid — default entry point. */
    public Position getDefaultSpawnPosition() {
        return grid.getAllPositions().stream()
                .filter(this::isWalkable)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nessuna tile camminabile in " + name));
    }

    public void removeFish(FishEntity fish) {
        activeFish.remove(fish);
    }

    /** True if there are no more active fish — the map is "cleared" for this visit. */
    public boolean isCleared() {
        return activeFish.isEmpty();
    }

    /** Delegations to the TileGrid — avoids exposing the grid directly. */
    public Tile    getTile(Position pos)  { return grid.getTile(pos);           }
    public boolean isWalkable(Position pos) { return grid.getTile(pos).isWalkable(); }
    public boolean isFishable(Position pos) { return grid.getTile(pos).isFishable(); }
    public boolean isValid(Position pos)  { return grid.isValid(pos);   }
}
