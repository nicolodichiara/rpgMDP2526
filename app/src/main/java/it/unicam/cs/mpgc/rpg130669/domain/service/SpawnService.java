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
 * Popola una mappa con pesci nelle spawn zone.
 * La selezione del tipo di pesce è pesata (weighted random).
 * La posizione è casuale tra le tile fishable della zona.
 */

public class SpawnService {

    private final Random random;

    public SpawnService(Random random){
        this.random = random;
    }

    /**
     * popola la mappa di gioco con FishEntity nelle rispettive aree di Spawn
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
     * Moltiplica il peso totale dei pesci nella FishPool(pesci totali) con un doubleRandom (0.0 - 1-0).
     * Quindi aggiunge entità fino a che il peso non ha raggiunto quella soglia.
     * @param zone Zona di spawn, che contiene l'insieme dei pesci.
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
     * @param map mappa di gioco
     * @param zone zona di spawn
     * @return lista di posizioni dove si può pescare
     *
     * scorre tutta la mappa e cerca le posizioni isFishable == true;
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
