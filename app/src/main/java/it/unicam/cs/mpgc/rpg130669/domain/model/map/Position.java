package it.unicam.cs.mpgc.rpg130669.domain.model.map;

/**
 * Posizione applicata sia al FishEntity che a Player.
 * Coordinata immutabile (riga, colonna) sulla griglia di gioco.
 */
public record Position(int row, int col) {

    public Position translate(int dRow, int dCol){
        if (this.row + dRow < 0) throw new IllegalStateException("valore della riga negativo");
        if (this.col + dCol < 0) throw new IllegalStateException("valore della colonna negativo");
        return new Position(this.row+dRow, this.col+dCol);
    }

    /**
     * distanza minima su otto direzioni (sopra-sotto, dx-sx, diagonali) chiamata distanza Chebyshev
     */
    public int distanceTo(Position other){
        return Math.max(Math.abs(this.col - other.col),Math.abs(this.row - other.row));
    }

    public boolean adjacentPosition(Position other){
        return distanceTo(other) == 1;
    }

}
