package it.unicam.cs.mpgc.rpg130669.domain.model.item;

import java.util.List;
import java.util.Objects;

/**
 * Accessorio equipaggiabile in slot passivo.
 * Applica una lista di StatModifier alle stat del giocatore finché è equipaggiato.
 * Durabilità infinita (-1) — gli accessori non si consumano.
 */
public class Accessory extends AbstractItem {

    private final List<StatModifier> modifiers;

    public Accessory(String id, String name, String description,
                     List<StatModifier> modifiers) {
        super(id, name, description, -1);
        if (modifiers == null || modifiers.isEmpty())
            throw new IllegalArgumentException("Un accessorio deve avere almeno un modificatore");
        this.modifiers = List.copyOf(modifiers);
    }

    public List<StatModifier> getModifiers() { return modifiers; }
}
