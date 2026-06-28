package it.unicam.cs.mpgc.rpg130669.domain.model.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Grid of Tiles, generated with default tiles or from an XML file.
 * Manages checks on Position.
 */

public class TileGrid {

    private final Tile[][] grid;
    private final int rows;
    private final int cols;


    /**
     * Constructor that creates the grid with default Tiles.
     * @param rows
     * @param cols
     * @param defaultTile
     */
    public TileGrid(int rows, int cols, Tile defaultTile){

        gridCheck(rows, cols, defaultTile);

        this.cols = cols;
        this.rows = rows;
        this.grid = new Tile[rows][cols];

        for (int i=0; i < this.rows; i++){
            for (int j = 0; j < this.cols; j++){
                this.grid[i][j] = defaultTile;
            }
        }
    }

    /**
     * Constructor that initializes the grid layout from an XML file and creates a copy of it.
     * @param tiles
     */
    public TileGrid(Tile[][] tiles){
        Objects.requireNonNull(tiles, "tiles non deve essere null");

        if (tiles.length == 0) {
            throw new IllegalArgumentException("L'array tiles non può essere vuoto");
        }
        
        // row validation
        int expectedCols = tiles[0].length;
        for (int i = 0; i < tiles.length; i++) {
            Objects.requireNonNull(tiles[i], "La riga " + i + " non può essere null");
            if (tiles[i].length != expectedCols) {
                throw new IllegalArgumentException("Tutte le righe devono avere la stessa lunghezza");
            }
        }
        
        this.rows = tiles.length;
        this.cols = tiles[0].length;
        this.grid = new Tile[rows][cols];

        for (int i=0; i < this.rows; i++){
            for (int j = 0; j < this.cols; j++){
                Objects.requireNonNull(tiles[i][j], "la tile in [" + i + "] [" + j + "] è null");
                this.grid[i][j] = tiles[i][j];
            }
        }
    }

    public int getRows(){ return rows; }
    public int getCols(){ return cols; }

    public Tile getTile(Position position) {
        validatePositions(position);
        return grid[position.row()][position.col()];
    }

    public void setTile(Position position, Tile tile){
        validatePositions(position);
        grid[position.row()][position.col()] = tile;
    }

    /**
     * Alternative using separate coordinates.
     */

    public Tile getTile(int row, int col) {
        return getTile(new Position(row, col));
    }

    /**
     * Bounds checking or validation for a position.
     */

    public boolean isValid(Position position){
        return position != null
                && (position.row() >= 0 && position.row() < rows)  // Corretto: AND
                && (position.col() >= 0 && position.col() < cols); // Corretto: AND
    }

    /**
     * List of occupied positions near an entity (8 adjacent tiles).
     */
    public List<Position> getNeighbors(Position pos){
        
        validatePositions(pos);
        List<Position> neighbors = new ArrayList<>();
        
        for (int i = pos.row() - 1 ; i <= pos.row() + 1; i++){
            for (int j = pos.col() - 1; j <= pos.col() + 1; j++){
                
                if (i == pos.row() && j == pos.col()) continue;

                Position neighbor = new Position(i,j);
                if (isValid(neighbor)) neighbors.add(neighbor);
            }
        }
        return neighbors;
    }
    
    public List<Position> getAllPositions(){
        List<Position> allPositions = new ArrayList<>(rows*cols);
        for (int i=0; i < this.rows; i++)
            for (int j = 0; j < this.cols; j++)
                allPositions.add(new Position(i, j));
        return allPositions;
    }
    
    private void gridCheck(int a, int b, Tile defaultTile){
        if (a <= 0 || b <= 0 ) throw new IllegalArgumentException("valori per la griglia non validi");
        Objects.requireNonNull(defaultTile, "defaultTile non può essere null");
    }

    private void validatePositions(Position position){
        if (!isValid(position)) {
            throw new IllegalArgumentException("Posizione non valida: " + position + 
                " (limiti: 0-" + (rows-1) + ", 0-" + (cols-1) + ")");
        }
    }
}