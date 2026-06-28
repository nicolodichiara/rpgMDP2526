package it.unicam.cs.mpgc.rpg130669.domain.model.map;

/**
 * Position applied to both FishEntity and Player.
 * Immutable coordinate (row, column) on the game grid.
 */
public record Position(int row, int col) {

    public Position translate(int dRow, int dCol){
        if (this.row + dRow < 0) throw new IllegalStateException("valore della riga negativo");
        if (this.col + dCol < 0) throw new IllegalStateException("valore della colonna negativo");
        return new Position(this.row+dRow, this.col+dCol);
    }

    /**
     * Minimum distance across eight directions (up-down, left-right, diagonals) called Chebyshev distance.
     */
    public int distanceTo(Position other){
        return Math.max(Math.abs(this.col - other.col),Math.abs(this.row - other.row));
    }

    public boolean adjacentPosition(Position other){
        return distanceTo(other) == 1;
    }

}
