package it.unicam.cs.mpgc.rpg130669.domain.service;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishEntity;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishVisibility;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Stat;

/**
 * Calcolo della visibilità di un pesce per il giocatore.
 * Funzionamento: perceptionThreshold - player.PERCEPTION --> delta
 *  delta <= 0  → VISIBLE
 *  delta <= 2  → SILHOUETTE
 *  delta <= 5  → SHADOW
 *  delta > 5   → HIDDEN
 *
 *  La logica è:
 *  - separata dalle entità in quanto dipende solo da due oggetti distinti:
 *    Player e Fishentity
 *  - stateLess, i metodi sono puri
 */

public class VisibilityService {
    public FishVisibility compute(FishEntity fish, Player player) {
        int delta = fish.getTemplate().perceptionThreshold()
                - player.getStat(Stat.PERCEPTION);

        if (delta <= 0) return FishVisibility.CLEAR;
        if (delta <= 2) return FishVisibility.SILHOUETTE;
        if (delta <= 5) return FishVisibility.SHADOW;
        return FishVisibility.HIDDEN;
    }}
