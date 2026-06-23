package it.unicam.cs.mpgc.rpg130669.domain.model;

import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    @DisplayName("Dovrebbe creare una Position valida")
    void testValidPosition() {
        Position pos = new Position(5, 3);
        assertEquals(5, pos.row());
        assertEquals(3, pos.col());
    }

    @Test
    @DisplayName("Dovrebbe accettare valori zero")
    void testZeroValues() {
        assertDoesNotThrow(() -> new Position(0, 0));
        Position pos = new Position(0, 0);
        assertEquals(0, pos.row());
        assertEquals(0, pos.col());
    }

    @Test
    @DisplayName("Dovrebbe tradurre la posizione correttamente")
    void testTranslate() {
        Position original = new Position(5, 3);
        Position translated = original.translate(2, -1);

        assertEquals(7, translated.row());
        assertEquals(2, translated.col());
    }

    @Test
    @DisplayName("Dovrebbe lanciare eccezione se translate produce valori negativi")
    void testTranslateToNegative() {
        Position pos = new Position(1, 1);
        assertThrows(IllegalStateException.class, () -> pos.translate(-2, 0));
        assertThrows(IllegalStateException.class, () -> pos.translate(0, -2));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 1, 1, 1",
            "0, 0, 3, 4, 4",
            "2, 2, 2, 5, 3",
            "1, 1, 4, 1, 3",
            "0, 0, 0, 0, 0"
    })
    @DisplayName("Dovrebbe calcolare la distanza correttamente")
    void testDistanceTo(int r1, int c1, int r2, int c2, int expectedDistance) {
        Position pos1 = new Position(r1, c1);
        Position pos2 = new Position(r2, c2);
        assertEquals(expectedDistance, pos1.distanceTo(pos2));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 1, 1, true",
            "0, 0, 1, 0, true",
            "0, 0, 0, 1, true",
            "2, 2, 3, 3, true",
            "0, 0, 2, 2, false",
            "0, 0, 0, 0, false"
    })
    @DisplayName("Dovrebbe identificare posizioni adiacenti")
    void testAdjacentPosition(int r1, int c1, int r2, int c2, boolean expected) {
        Position pos1 = new Position(r1, c1);
        Position pos2 = new Position(r2, c2);
        assertEquals(expected, pos1.adjacentPosition(pos2));
    }

    @Test
    @DisplayName("Test di equals e hashCode")
    void testEqualsAndHashCode() {
        Position pos1 = new Position(3, 4);
        Position pos2 = new Position(3, 4);
        Position pos3 = new Position(3, 5);

        assertEquals(pos1, pos2);
        assertNotEquals(pos1, pos3);
        assertEquals(pos1.hashCode(), pos2.hashCode());
    }
}

