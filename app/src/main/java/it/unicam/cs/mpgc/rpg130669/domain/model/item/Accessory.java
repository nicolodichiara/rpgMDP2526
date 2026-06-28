package it.unicam.cs.mpgc.rpg130669.domain.model.item;

import java.util.List;
import java.util.Objects;

/**
 * Equippable accessory for a passive slot.
 * Applies a list of StatModifiers to the player's stats as long as it is equipped.
 * Infinite durability (-1) — accessories do not wear out.
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
