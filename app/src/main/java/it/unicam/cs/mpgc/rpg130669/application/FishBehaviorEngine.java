package it.unicam.cs.mpgc.rpg130669.application;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishBehaviorState;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishEntity;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.GameMap;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;

import java.util.List;
import java.util.Random;

/**
 * Updates the position and behavioral state of all active fish
 * after each player action.
 *
 * Note: does not modify GameMap or Player — works exclusively on FishEntity.
 */
public class FishBehaviorEngine {
   private final Random random;

   public FishBehaviorEngine(Random random){
       this.random = random;
   }

    /**
     * Entry point called by GameSessionUseCase after each action.
     * @param lastCastPosition position of the cast bait, null if there is no active cast.
     */
    public void update(GameMap map, Position playerPosition, Position lastCastPosition) {
        for (FishEntity fish : map.getActiveFish()) {
            updateState(fish, playerPosition);
            move(fish, map, playerPosition, lastCastPosition);
        }
    }

    private void updateState(FishEntity fish, Position playerPos) {
        var profile = fish.getTemplate().behaviorProfile();
        boolean inRange = !fish.isOutOfRange(playerPos);

        if (!inRange) {
            // out of range: random behavior between WANDERING and IDLE
            fish.setBehaviorState(
                    random.nextFloat() < profile.wanderRate()
                            ? FishBehaviorState.WANDERING
                            : FishBehaviorState.IDLE
            );
            return;
        }
        // In range: profile-based transition
        fish.setBehaviorState(switch (fish.getBehaviorState()) {
            case IDLE, WANDERING, NEUTRAL ->
                    random.nextFloat() < profile.fearThreshold()
                            ? FishBehaviorState.SCARED
                            : random.nextFloat() < profile.curiosity()
                            ? FishBehaviorState.ATTRACTED
                            : FishBehaviorState.NEUTRAL;
            case ATTRACTED ->
                    random.nextFloat() < profile.fearThreshold()
                            ? FishBehaviorState.SCARED
                            : FishBehaviorState.ATTRACTED;
            case SCARED    -> FishBehaviorState.FLEEING;
            case FLEEING   ->
                    fish.isOutOfRange(playerPos)
                            ? FishBehaviorState.IDLE
                            : FishBehaviorState.FLEEING;
        });
    }

    // resolver FishEntity behavior
    private void move(FishEntity fish, GameMap map,
                      Position playerPos, Position castPos) {
        Position next = switch (fish.getBehaviorState()) {
            case WANDERING       -> randomFishableNeighbor(fish.getPosition(), map);
            case ATTRACTED       -> castPos != null ? stepToward(fish.getPosition(), castPos, map) : null;
            case SCARED, FLEEING -> stepAwayFrom(fish.getPosition(), playerPos, map);
            default              -> null;   // IDLE, NEUTRAL: non si muove
        };

        if (next != null) fish.setPosition(next);
    }

    private Position stepToward(Position from, Position target, GameMap map) {
        return bestNeighbor(from, target, map, true);
    }

    private Position stepAwayFrom(Position from, Position avoid, GameMap map) {
        return bestNeighbor(from, avoid, map, false);
    }

    /**
     * Chooses the adjacent fishable tile that minimizes (toward=true) or
     * maximizes (toward=false) the distance to the target.
     */
    private Position bestNeighbor(Position from, Position target,
                                  GameMap map, boolean toward) {
        List<Position> neighbors = map.getGrid().getNeighbors(from);
        Position best = null;
        int bestDist  = toward ? Integer.MAX_VALUE : Integer.MIN_VALUE;

        for (Position n : neighbors) {
            if (!map.isFishable(n)) continue;
            int dist = n.distanceTo(target);
            if (toward  && dist < bestDist) { bestDist = dist; best = n; }
            if (!toward && dist > bestDist) { bestDist = dist; best = n; }
        }
        return best;
    }

    private Position randomFishableNeighbor(Position pos, GameMap map) {
        List<Position> candidates = map.getGrid().getNeighbors(pos).stream()
                .filter(map::isFishable)
                .toList();
        if (candidates.isEmpty()) return null;
        return candidates.get(random.nextInt(candidates.size()));
    }
}
