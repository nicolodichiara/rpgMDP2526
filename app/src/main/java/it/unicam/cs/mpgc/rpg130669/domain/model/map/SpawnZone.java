package it.unicam.cs.mpgc.rpg130669.domain.model.map;

/**
 * Delimita la zona dove avviene lo spawn dei Pesci:
 * Definita da un rettangolo di cui sappiamo il 'top-left' e il 'bottom-right'
 * WeightedFish associa un FishTemplate a un peso relativo usato
 * dallo SpawnService per la selezione casuale pesata.
 */


import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishTemplate;

import java.util.List;
import java.util.Objects;

public class SpawnZone {

    private final Position         bottomRight;
    private final List<WeightedFish> fishPool;
    private final Position         topLeft;
    private final int              maxFish;

    public SpawnZone(Position topLeft, Position bottomRight, List<WeightedFish> fishPool, int maxFish) {
        this.topLeft     = Objects.requireNonNull(topLeft,     "topLeft non può essere null");
        this.bottomRight = Objects.requireNonNull(bottomRight, "bottomRight non può essere null");
        this.fishPool    = List.copyOf(fishPool);
        if (maxFish <= 0) throw new IllegalArgumentException("maxFish deve essere > 0: " + maxFish);
        this.maxFish     = maxFish;

        if (topLeft.row() > bottomRight.row() || topLeft.col() > bottomRight.col())
            throw new IllegalArgumentException("topLeft deve essere in alto a sinistra di bottomRight");
    }

    public boolean contains(Position pos) {
        return pos.row() >= topLeft.row() && pos.row() <= bottomRight.row()
                && pos.col() >= topLeft.col() && pos.col() <= bottomRight.col();
    }

    /**
     * @return area di spawn
     */
    public int area() {
        return (bottomRight.row() - topLeft.row() + 1)
                * (bottomRight.col() - topLeft.col() + 1);
    }

    public int getMaxFish(){
        return maxFish;
    }
    public List<WeightedFish> getFishPool(){
        return fishPool;
    }

    public Position getTopLeft() {
        return topLeft;
    }
    public Position getBottomRight(){
        return bottomRight;
    }

    public record WeightedFish(FishTemplate template, float weight){

        public WeightedFish{
            Objects.requireNonNull(template, "template non può essere null");
            if (weight <= 0f) throw new IllegalArgumentException("weight deve essere maggiore di zero");
        }
    }
}
