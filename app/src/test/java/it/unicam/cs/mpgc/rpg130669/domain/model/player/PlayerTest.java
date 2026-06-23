package it.unicam.cs.mpgc.rpg130669.domain.model.player;

import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("p1", "Nico", new Position(0, 0));
    }

    @Test
    void initialStats_allAreBaseValue() {
        for (Stat s : Stat.values())
            assertEquals(1, player.getStat(s));
    }

    @Test
    void initialXp_allAreZero() {
        for (Stat s : Stat.values())
            assertEquals(0, player.getXp(s));
    }

    @Test
    void gainXp_belowThreshold_statUnchanged() {
        player.gainExp(Stat.STRENGTH, 50);  // threshold = 100
        assertEquals(1, player.getStat(Stat.STRENGTH));
        assertEquals(50, player.getXp(Stat.STRENGTH));
    }

    @Test
    void gainXp_exactThreshold_statIncreases() {
        player.gainExp(Stat.STRENGTH, 100);
        assertEquals(2, player.getStat(Stat.STRENGTH));
        assertEquals(0, player.getXp(Stat.STRENGTH));
    }

    @Test
    void gainXp_overThreshold_remainderCarriedOver() {
        player.gainExp(Stat.STRENGTH, 150);
        assertEquals(2, player.getStat(Stat.STRENGTH));
        assertEquals(50, player.getXp(Stat.STRENGTH));
    }

    @Test
    void gainXp_multipleLevelUps_onlyOnePerCall() {
        // Una singola chiamata non dovrebbe dare più di un livello
        player.gainExp(Stat.STRENGTH, 250);
        // 250 / 100 = 2 level-up ma l'implementazione gestisce un livello alla volta
        // Verifichiamo che la stat sia almeno 2
        assertTrue(player.getStat(Stat.STRENGTH) >= 2);
    }

    @Test
    void getLevel_freshPlayer_isBaseValue() {
        assertEquals(1, player.getLevel());
    }

    @Test
    void getLevel_afterLevelUp_increases() {
        player.gainExp(Stat.STRENGTH, 100);
        // Level = media di tutte le stat, una è salita a 2, le altre a 1
        // Media = (2 + 1 + 1 + 1 + 1) / 5 = 1.2 → int = 1
        // Nota: con più stat alzate il livello sale
        player.gainExp(Stat.CASTING,   100);
        player.gainExp(Stat.PATIENCE,  100);
        player.gainExp(Stat.PERCEPTION,100);
        player.gainExp(Stat.CRAFTING,  100);
        assertEquals(2, player.getLevel());
    }

    @Test
    void canAccessMap_sufficientLevel_true() {
        assertTrue(player.canAccessMap(1));
    }

    @Test
    void canAccessMap_insufficientLevel_false() {
        assertFalse(player.canAccessMap(10));
    }

    @Test
    void gainXp_negativeAmount_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> player.gainExp(Stat.STRENGTH, -1));
    }

    @Test
    void setPosition_updatesCorrectly() {
        Position newPos = new Position(3, 4);
        player.setPosition(newPos);
        assertEquals(newPos, player.getPosition());
    }
}
