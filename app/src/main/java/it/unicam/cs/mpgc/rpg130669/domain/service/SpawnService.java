package it.unicam.cs.mpgc.rpg130669.domain.service;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishEntity;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishTemplate;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.GameMap;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.SpawnZone;
import it.unicam.cs.mpgc.rpg130669.domain.model.world.WorldClock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Populates a map with fish in the designated spawn zones.
 * Fish species selection is based on a weighted random system.
 * The spawn position is chosen randomly from the fishable tiles within the zone.
 */

public class SpawnService {

    private final Random random;

    public SpawnService(Random random){
        this.random = random;
    }

    /**
     * Populates the game map with FishEntities inside their respective Spawn areas.
     */

    public void populate(GameMap map, WorldClock clock){
        for (SpawnZone zone : map.getSpawnZones()){
            long fishInZone = map.getActiveFish().stream()
                    .filter(f->zone.contains(f.getPosition()))
                    .count();
            int toSpawn = zone.getMaxFish() - (int) fishInZone;
            for (int i = 0; i < toSpawn ; i++){
                FishTemplate template = selectWeighted(zone);
                Position pos = randomFishablePosition(map, zone);
                if (pos != null) map.addFish(new FishEntity(pos, template));
            }
        }
    }

    /**
     * Multiplies the total weight of the fish in the FishPool (total fish pool) by a random double (0.0 - 1.0).
     * It then adds entities sequentially until the running weight reaches that threshold.
     * @param zone The spawn zone, which contains the set of available fish.
     */

    private FishTemplate selectWeighted(SpawnZone zone){
        double total = zone.getFishPool().stream()
                .mapToDouble(SpawnZone.WeightedFish::weight).sum();
        double roll = random.nextDouble() * total;
        double comulative = 0;
        for (SpawnZone.WeightedFish wf : zone.getFishPool()){
            comulative += wf.weight();
            if (roll <= comulative) return wf.template();
        }
        return zone.getFishPool().getLast().template();
    }

    /**
     * @param map the game map
     * @param zone the spawn zone
     * @return a list of positions where fishing is allowed
     *
     * Iterates through the entire map to find positions where isFishable == true.
     */

    private Position randomFishablePosition(GameMap map, SpawnZone zone) {
        List<Position> candidates = new ArrayList<>();
        for (int r = zone.getTopLeft().row(); r <= zone.getBottomRight().row(); r++)
            for (int c = zone.getTopLeft().col(); c <= zone.getBottomRight().col(); c++) {
                Position p = new Position(r, c);
                if (map.isFishable(p)) candidates.add(p);
            }
        if (candidates.isEmpty()) return null;
        return candidates.get(random.nextInt(candidates.size()));
    }
}
