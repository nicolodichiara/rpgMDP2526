package it.unicam.cs.mpgc.rpg130669.domain.model.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TileGridTest {

    private TileGrid grid;
    private static final Tile GRASS = new Tile(TileType.GRASS);
    private static final Tile WATER = new Tile(TileType.WATER);

    @BeforeEach
    void setUp() {
        grid = new TileGrid(5, 5, GRASS);
    }

    @Test
    void constructor_negativeDimensions_throws() {
        assertThrows(IllegalArgumentException.class, () -> new TileGrid(0, 5, GRASS));
        assertThrows(IllegalArgumentException.class, () -> new TileGrid(5, 0, GRASS));
    }

    @Test
    void constructor_nullDefaultTile_throws() {
        assertThrows(NullPointerException.class, () -> new TileGrid(5, 5, null));
    }

    @Test
    void getTile_validPosition_returnsCorrectTile() {
        assertEquals(GRASS, grid.getTile(new Position(0, 0)));
        assertEquals(GRASS, grid.getTile(new Position(4, 4)));
    }

    @Test
    void setTile_updatesCorrectly() {
        Position pos = new Position(2, 3);
        grid.setTile(pos, WATER);
        assertEquals(WATER, grid.getTile(pos));
    }

    @Test
    void getTile_outOfBounds_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> grid.getTile(new Position(5, 0)));
    }

    @Test
    void isValidPosition_boundaryCells_true() {
        assertTrue(grid.isValid(new Position(0, 0)));
        assertTrue(grid.isValid(new Position(4, 4)));
    }
    @Test
    void isValidPosition_outOfBounds_false() {
        assertFalse(grid.isValid(new Position(5, 0)));
        assertFalse(grid.isValid(new Position(0, 5)));
    }

    @Test
    void getNeighbors_cornerTile_returns3() {
        List<Position> neighbors = grid.getNeighbors(new Position(0, 0));
        assertEquals(3, neighbors.size());
    }

    @Test
    void getNeighbors_edgeTile_returns5() {
        List<Position> neighbors = grid.getNeighbors(new Position(0, 2));
        assertEquals(5, neighbors.size());
    }

    @Test
    void getNeighbors_centerTile_returns8() {
        List<Position> neighbors = grid.getNeighbors(new Position(2, 2));
        assertEquals(8, neighbors.size());
    }

    @Test
    void getAllPositions_returnsRowsTimesCols() {
        assertEquals(25, grid.getAllPositions().size());
    }
}