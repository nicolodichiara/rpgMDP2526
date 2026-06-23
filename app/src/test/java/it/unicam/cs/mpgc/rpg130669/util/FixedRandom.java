package it.unicam.cs.mpgc.rpg130669.util;

import java.util.Random;

/**
 * Random deterministico per i test.
 * Consuma la sequenza fornita in ordine ciclico.
 * nextInt() ritorna sempre 0 — utile per selezioni di lista.
 *
 * Classe implementata dopo valutazione di un risposta Ai:
 * "Le versioni recenti di Java hanno restrizioni al bytecode manipulation usato da Mockito per classi di sistema.
 *  Estendere Random è più stabile su Java 25 e non aggiunge dipendenze esterne ai test."
 */
public class FixedRandom extends Random {

    private final double[] seq;
    private int index = 0;

    public FixedRandom(double... seq) {
        this.seq = seq;
    }

    @Override public double nextDouble() { return seq[index++ % seq.length]; }
    @Override public float  nextFloat()  { return (float) seq[index++ % seq.length]; }
    @Override public int    nextInt(int n){ return 0; }
}
