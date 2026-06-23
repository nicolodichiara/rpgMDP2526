
    package it.unicam.cs.mpgc.rpg130669.application.usecase;

import it.unicam.cs.mpgc.rpg130669.application.CombatEngine;
import it.unicam.cs.mpgc.rpg130669.domain.model.combat.*;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishEntity;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Stat;
import it.unicam.cs.mpgc.rpg130669.util.FixedRandom;
import it.unicam.cs.mpgc.rpg130669.util.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

    class CombatEngineTest {

        /*
         * selectFishAction con fear=0.5 (defaultProfile):
         *   roll < 0.20 → SWIM_AWAY
         *   roll < 0.35 → STRUGGLE      ← FixedRandom(0.25)
         *   roll < 0.50 → BITE_HARDER   ← FixedRandom(0.40)
         *   roll >= 0.50 → TIRE         ← FixedRandom(0.90)
         */
        private static final double ROLL_STRUGGLE    = 0.25;
        private static final double ROLL_BITE_HARDER = 0.40;
        private static final double ROLL_TIRE        = 0.90;
        private static final double ROLL_SWIM_AWAY   = 0.10;

        private Player     player;
        private FishEntity fish;

        @BeforeEach
        void setUp() {
            player = TestFixtures.playerAt(new Position(0, 0));
            fish   = TestFixtures.fishAt(new Position(2, 2));
        }

        private FishingSession newSession(CombatEngine engine) {
            return newSession(engine, TestFixtures.defaultRod());
        }

        private FishingSession newSession(CombatEngine engine,
                                          it.unicam.cs.mpgc.rpg130669.domain.model.item.FishingRod rod) {
            FishingSession s = new FishingSession(player, fish, rod);
            s.startCombat();
            return s;
        }

        // ── PULL ──────────────────────────────────────────────────────────────────

        @Test
        void pull_dealsDamage_basedOnStrengthMinusFishStrength() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = newSession(engine);

            int hpBefore = fish.getHp();
            engine.applyPlayerAction(session, PlayerAction.PULL, null);

            // damage = 5 (base) + 1 (STRENGTH) - 3 (combatStrength) = 3
            assertEquals(hpBefore - 3, fish.getHp());
        }

        @Test
        void pull_grantsStrengthAndCastingXp() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = newSession(engine);

            engine.applyPlayerAction(session, PlayerAction.PULL, null);

            assertEquals(5, player.getXp(Stat.STRENGTH));
            assertEquals(2, player.getXp(Stat.CASTING));
        }

        @Test
        void pull_decreasesVirtualDistance() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = newSession(engine);
            session.getCombatState().increaseDistance(3);

            engine.applyPlayerAction(session, PlayerAction.PULL, null);

            assertEquals(2, session.getCombatState().getVirtualDistance());
        }

        @Test
        void pull_dealAtLeastOneDamage_evenIfStrengthLow() {
            // Fish con combatStrength molto alto — danno minimo garantito a 1
            var strongFishTemplate = new it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishTemplate(
                    "strong", "Bestione", it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishRarity.LEGENDARY,
                    TestFixtures.defaultProfile(), 100, 100, 99, 0);
            FishEntity strongFish = new FishEntity(new Position(2, 2), strongFishTemplate);

            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = new FishingSession(player, strongFish,
                    TestFixtures.defaultRod());
            session.startCombat();

            int hpBefore = strongFish.getHp();
            engine.applyPlayerAction(session, PlayerAction.PULL, null);

            assertEquals(hpBefore - 1, strongFish.getHp());
        }

        // ── WAIT ─────────────────────────────────────────────────────────────────

        @Test
        void wait_reduceFishStamina() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = newSession(engine);

            int staminaBefore = fish.getStamina();
            engine.applyPlayerAction(session, PlayerAction.WAIT, null);

            assertEquals(staminaBefore - 3, fish.getStamina());
        }

        @Test
        void wait_grantsPatience() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = newSession(engine);

            engine.applyPlayerAction(session, PlayerAction.WAIT, null);

            assertEquals(8, player.getXp(Stat.PATIENCE));
        }

        // ── USE_BAIT ─────────────────────────────────────────────────────────────

        @Test
        void useBait_universalBait_reducesStamina() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = newSession(engine);

            int staminaBefore = fish.getStamina();
            engine.applyPlayerAction(session, PlayerAction.USE_BAIT,
                    TestFixtures.universalBait());

            // staminaDrain = (int)(0.5 * 20) = 10
            assertEquals(staminaBefore - 10, fish.getStamina());
        }

        @Test
        void useBait_specificBait_wrongRarity_noEffect() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = newSession(engine); // pesce COMMON

            int staminaBefore = fish.getStamina();
            engine.applyPlayerAction(session, PlayerAction.USE_BAIT,
                    TestFixtures.rareBait()); // esca per RARE

            assertEquals(staminaBefore, fish.getStamina());
        }

        @Test
        void useBait_nullItem_addsEffectNoException() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = newSession(engine);

            assertDoesNotThrow(() ->
                    engine.applyPlayerAction(session, PlayerAction.USE_BAIT, null));
        }

        // ── GIVE_UP ──────────────────────────────────────────────────────────────

        @Test
        void giveUp_setsSessionToGivenUp() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = newSession(engine);

            engine.applyPlayerAction(session, PlayerAction.GIVE_UP, null);

            assertEquals(SessionState.GIVEN_UP, session.getSessionState());
        }

        @Test
        void giveUp_sessionIsNotActive() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = newSession(engine);

            engine.applyPlayerAction(session, PlayerAction.GIVE_UP, null);

            assertFalse(session.isActive());
            assertTrue(session.isConcluded());
        }

        // ── OUTCOME ──────────────────────────────────────────────────────────────

        @Test
        void fishDefeated_afterPull_sessionIsCaught() {
            fish = TestFixtures.weakFishAt(new Position(2, 2)); // HP = 1
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = new FishingSession(player, fish,
                    TestFixtures.defaultRod());
            session.startCombat();

            engine.applyPlayerAction(session, PlayerAction.PULL, null);

            assertEquals(SessionState.CAUGHT, session.getSessionState());
        }

        @Test
        void rodBroken_afterFishStruggle_sessionIsEscaped() {
            // Un solo elemento: l'unica chiamata a random avviene in
            // selectFishAction durante applyFishTurn — PULL/WAIT non la usano.
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_STRUGGLE));
            FishingSession session = newSession(engine, TestFixtures.fragileRod());

            engine.applyPlayerAction(session, PlayerAction.WAIT, null);
            engine.applyFishTurn(session); // ROLL_STRUGGLE → STRUGGLE → -3 durability → rotta

            assertEquals(SessionState.ESCAPED, session.getSessionState());
        }


        @Test
        void fishTooFar_afterSwimAway_sessionIsEscaped() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_SWIM_AWAY));
            FishingSession session = newSession(engine);
            session.getCombatState().increaseDistance(9);

            engine.applyPlayerAction(session, PlayerAction.WAIT, null);
            engine.applyFishTurn(session); // ROLL_SWIM_AWAY → SWIM_AWAY → +2 → 11 > 10 → ESCAPED

            assertEquals(SessionState.ESCAPED, session.getSessionState());
        }

        // ── GUARDIE ──────────────────────────────────────────────────────────────

        @Test
        void applyPlayerAction_whenNotPlayerTurn_throws() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = new FishingSession(player, fish,
                    TestFixtures.defaultRod());
            // Non chiamiamo startCombat() → stato CASTING, non PLAYER_TURN

            assertThrows(IllegalStateException.class, () ->
                    engine.applyPlayerAction(session, PlayerAction.PULL, null));
        }

        @Test
        void applyFishTurn_whenNotFishTurn_throws() {
            CombatEngine engine = new CombatEngine(new FixedRandom(ROLL_TIRE));
            FishingSession session = newSession(engine); // PLAYER_TURN, non FISH_TURN

            assertThrows(IllegalStateException.class, () ->
                    engine.applyFishTurn(session));
        }
    }

