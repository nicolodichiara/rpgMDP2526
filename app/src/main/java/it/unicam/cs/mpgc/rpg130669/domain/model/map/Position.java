package it.unicam.cs.mpgc.rpg130669.domain.model.map;

    //   non vengono effettuati controlli di validità sui valori nei metodi
    //   che vanno a cambiare il valore della posizione (responsabilità esterna)
public record Position(int row, int col) {
    public Position{
        if (row < 0) throw new IllegalStateException("valore della riga negativo" + row);
        if (col < 0) throw new IllegalStateException("valore della colonna negativo" + col);
    }

    public Position translate(int dRow, int dCol){
        return new Position(this.row+dRow, this.col+dCol);
    }


    //   Movimento in 8 direzioni: Rappresenta il numero minimo di mosse
    //   necessarie per raggiungere un punto da un altro, considerando che puoi
    //   muoverti in tutte le 8 direzioni (orizzontale, verticale e diagonale)

    public int distanceTo(Position other){
        return Math.max(Math.abs(this.col - other.col),Math.abs(this.row - other.row));
    }

    public boolean adjacentPosition(Position other){
        return distanceTo(other) == 1;
    }

}
