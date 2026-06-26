package it.unicam.cs.mpgc.rpg130669.domain.model.fish;

/**
 * Visibilità del pesce nella mappa
 * 1. CLEAR        — perfettamente visibile nella mappa
 * 2. SILHOUETTE   — forma e colore differente
 * 3. SHADOW       — sagoma scurita del pesce
 * 4. HIDDEN       — non visibile in mappa
 */

public enum FishVisibility {
    CLEAR,
    SILHOUETTE,
    SHADOW,
    HIDDEN
}
