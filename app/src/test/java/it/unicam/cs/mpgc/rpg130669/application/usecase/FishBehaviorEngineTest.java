package it.unicam.cs.mpgc.rpg130669.application.usecase;

import it.unicam.cs.mpgc.rpg130669.application.FishBehaviorEngine;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.*;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.*;
import it.unicam.cs.mpgc.rpg130669.util.FixedRandom;
import it.unicam.cs.mpgc.rpg130669.util.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FishBehaviorEngineTest {

    /*
     * updateState usa nextFloat() due volte per pesci in range:
     *   1° nextFloat() → confronto con fearThreshold
     *   2° nextFloat() → confronto con curiosity (solo se non spaventato)
     * quindi avremo sempre un primo controllo paura -> curiosita
     *
     * esempio:
     * curiousProfile: curiosity=0.99, fearThreshold=0.01
     *   roll=0.90 > 0.01 → non scared → roll=0.90 > 0.99 → NEUTRAL
     *   roll=0.001 < 0.01 → SCARED
     *   roll=0.90, then roll=0.50 < 0.99 → ATTRACTED
     *
     * scaredProfile: fearThreshold=0.99
     *   roll=0.50 < 0.99 → SCARED
     */

    @Test
    void fishOutOfRange_remainsIdleOrWandering() {
        GameMap map = TestFixtures.allWaterMap(10);
        Position playerPos = new Position(0, 0);
        Position fishPos   = new Position(9, 9); // distanza Chebyshev = 9 > reactionRange=5

        FishEntity fish = TestFixtures.fishAt(fishPos);
        fish.setBehaviorState(FishBehaviorState.NEUTRAL);
        map.addFish(fish);

        // wanderRate=0.3: con roll=0.90 > 0.3 → IDLE
        FishBehaviorEngine engine = new FishBehaviorEngine(new FixedRandom(0.90));
        engine.update(map, playerPos, null);

        assertEquals(FishBehaviorState.IDLE, fish.getBehaviorState());
    }

    @Test
    void fishOutOfRange_highWanderRate_becomesWandering() {
        GameMap map = TestFixtures.allWaterMap(10);
        FishEntity fish = TestFixtures.fishAt(new Position(9, 9));
        map.addFish(fish);

        // roll=0.10 < wanderRate=0.3 → WANDERING
        FishBehaviorEngine engine = new FishBehaviorEngine(new FixedRandom(0.10));
        engine.update(map, new Position(0, 0), null);

        assertEquals(FishBehaviorState.WANDERING, fish.getBehaviorState());
    }

    @Test
    void fishInRange_withHighFear_becomesScared() {
        GameMap map = TestFixtures.allWaterMap(10);
        var scaredTemplate = new FishTemplate("s", "Merluzzo", FishRarity.COMMON,
                TestFixtures.scaredProfile(), 20, 20, 3, 0);
        FishEntity fish = new FishEntity(new Position(2, 2), scaredTemplate);
        map.addFish(fish);

        // fearThreshold=0.99: qualsiasi roll < 0.99 → SCARED
        FishBehaviorEngine engine = new FishBehaviorEngine(new FixedRandom(0.50));
        engine.update(map, new Position(0, 0), null);

        assertEquals(FishBehaviorState.SCARED, fish.getBehaviorState());
    }

    @Test
    void fishInRange_withHighCuriosity_becomesAttracted() {
        GameMap map = TestFixtures.allWaterMap(10);
        var curiousTemplate = new FishTemplate("c", "Carpa", FishRarity.COMMON,
                TestFixtures.curiousProfile(), 20, 20, 3, 0);
        FishEntity fish = new FishEntity(new Position(2, 2), curiousTemplate);
        map.addFish(fish);

        // 1° roll=0.50 > fearThreshold=0.01 → non scared
        // 2° roll=0.50 < curiosity=0.99 → ATTRACTED
        FishBehaviorEngine engine = new FishBehaviorEngine(new FixedRandom(0.50, 0.50));
        engine.update(map, new Position(0, 0), null);

        assertEquals(FishBehaviorState.ATTRACTED, fish.getBehaviorState());
    }

    @Test
    void attractedFish_movesTowardCastPosition() {
        GameMap map = TestFixtures.allWaterMap(10);

        Position fishPos  = new Position(5, 5);
        Position castPos  = new Position(5, 8); // a destra del pesce
        Position playerPos = new Position(0, 0);

        var curiousTemplate = new FishTemplate("c", "Carpa", FishRarity.COMMON,
                TestFixtures.curiousProfile(), 20, 20, 3, 0);
        FishEntity fish = new FishEntity(fishPos, curiousTemplate);
        fish.setBehaviorState(FishBehaviorState.ATTRACTED);
        map.addFish(fish);

        // 1° roll > fearThreshold=0.01 → non scared
        // 2° roll < curiosity=0.99 → rimane ATTRACTED → stepToward castPos
        FishBehaviorEngine engine = new FishBehaviorEngine(new FixedRandom(0.50, 0.50));
        engine.update(map, playerPos, castPos);

        // Il pesce deve essersi avvicinato al cast — distanza Chebyshev ridotta
        assertTrue(fish.getPosition().distanceTo(castPos)
                < fishPos.distanceTo(castPos));
    }

    @Test
    void fleeingFish_movesAwayFromPlayer() {
        GameMap map = TestFixtures.allWaterMap(10);

        Position fishPos   = new Position(3, 3);
        Position playerPos = new Position(0, 0);

        var scaredTemplate = new FishTemplate("s", "Merluzzo", FishRarity.COMMON,
                TestFixtures.scaredProfile(), 20, 20, 3, 0);
        FishEntity fish = new FishEntity(fishPos, scaredTemplate);
        fish.setBehaviorState(FishBehaviorState.FLEEING);
        map.addFish(fish);

        // roll=0.50 < fearThreshold=0.99 → rimane FLEEING → stepAwayFrom player
        FishBehaviorEngine engine = new FishBehaviorEngine(new FixedRandom(0.50));
        engine.update(map, playerPos, null);

        assertTrue(fish.getPosition().distanceTo(playerPos)
                > fishPos.distanceTo(playerPos));
    }

    @Test
    void idleFish_doesNotMove() {
        GameMap map = TestFixtures.allWaterMap(10);
        Position fishPos   = new Position(9, 9);
        FishEntity fish = TestFixtures.fishAt(fishPos);
        fish.setBehaviorState(FishBehaviorState.IDLE);
        map.addFish(fish);

        // roll=0.90 > wanderRate=0.3 → IDLE → no movimento
        FishBehaviorEngine engine = new FishBehaviorEngine(new FixedRandom(0.90));
        engine.update(map, new Position(0, 0), null);

        assertEquals(fishPos, fish.getPosition());
    }

    @Test
    void multipleFish_allUpdated() {
        GameMap map = TestFixtures.allWaterMap(10);
        FishEntity f1 = TestFixtures.fishAt(new Position(9, 9));
        FishEntity f2 = TestFixtures.fishAt(new Position(8, 8));
        map.addFish(f1);
        map.addFish(f2);

        new FishBehaviorEngine(new FixedRandom(0.90))
                .update(map, new Position(0, 0), null);

        // Entrambi i pesci devono essere stati processati — stato aggiornato
        assertNotNull(f1.getBehaviorState());
        assertNotNull(f2.getBehaviorState());
    }
}
