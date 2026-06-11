package it.unicam.cs.mpgc.rpg130669.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class TileTypeTest {

    @ParameterizedTest
    @CsvSource({
        "GRASS, grass, true, false",
        "DOCK, dock, true, true",
        "WATER, water, false, true",
        "DEEP_WATER, deep_water, false, true",
        "ROCK, rock, false, false",
        "SAND, sand, true, false"
    })
    @DisplayName("Dovrebbe avere proprietà corrette per ogni tipo")
    void testTileTypeProperties(TileType type, String code, boolean walkable, boolean fishable) {
        assertEquals(code, type.getCode());
        assertEquals(walkable, type.isWalkable());
        assertEquals(fishable, type.isFishable());
    }

    @ParameterizedTest
    @CsvSource({
        "grass, GRASS",
        "dock, DOCK", 
        "water, WATER",
        "deep_water, DEEP_WATER",
        "rock, ROCK",
        "sand, SAND"
    })
    @DisplayName("Dovrebbe trovare TileType dal codice")
    void testFromCode(String code, TileType expected) {
        assertEquals(expected, TileType.fromCode(code));
    }

    @Test
    @DisplayName("Dovrebbe lanciare eccezione per codice sconosciuto")
    void testFromCodeUnknown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> TileType.fromCode("unknown"));
        assertTrue(exception.getMessage().contains("tiletype non conosciuto"));
    }

    @Test
    @DisplayName("Dovrebbe gestire codice null")
    void testFromCodeNull() {
        assertThrows(IllegalArgumentException.class, () -> TileType.fromCode(null));
    }

    @Test
    @DisplayName("Dovrebbe avere tutti i valori attesi")
    void testAllValues() {
        TileType[] values = TileType.values();
        assertEquals(6, values.length);
        
        // Verifica che tutti i tipi attesi siano presenti
        assertTrue(java.util.Arrays.asList(values).contains(TileType.GRASS));
        assertTrue(java.util.Arrays.asList(values).contains(TileType.DOCK));
        assertTrue(java.util.Arrays.asList(values).contains(TileType.WATER));
        assertTrue(java.util.Arrays.asList(values).contains(TileType.DEEP_WATER));
        assertTrue(java.util.Arrays.asList(values).contains(TileType.ROCK));
        assertTrue(java.util.Arrays.asList(values).contains(TileType.SAND));
    }
}