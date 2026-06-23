package it.unicam.cs.mpgc.rpg130669.application.usecase;

import it.unicam.cs.mpgc.rpg130669.application.InventoryUseCase;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.*;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.util.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryUseCaseTest {

    private InventoryUseCase useCase;
    private Player player;

    @BeforeEach
    void setUp() {
        useCase = new InventoryUseCase();
        player  = TestFixtures.playerAt(new Position(0, 0));
    }

    // ── add ──────────────────────────────────────────────────────────────────

    @Test
    void addItem_newItem_quantityIsCorrect() {
        FishingRod rod = TestFixtures.defaultRod();
        useCase.addItem(rod, player, 1);
        assertEquals(1, player.getInventory().getQuantity(rod));
    }

    @Test
    void addItem_existingItem_quantityAccumulates() {
        FishingRod rod = TestFixtures.defaultRod();
        useCase.addItem(rod, player, 2);
        useCase.addItem(rod, player, 3);
        assertEquals(5, player.getInventory().getQuantity(rod));
    }

    @Test
    void addItem_zeroQuantity_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.addItem(TestFixtures.defaultRod(), player, 0));
    }

    // ── remove ───────────────────────────────────────────────────────────────

    @Test
    void removeItem_sufficientQuantity_ok() {
        Bait bait = TestFixtures.universalBait();
        useCase.addItem(bait, player, 5);
        useCase.removeItem(bait, player, 3);
        assertEquals(2, player.getInventory().getQuantity(bait));
    }

    @Test
    void removeItem_exactQuantity_itemRemovedFromInventory() {
        Bait bait = TestFixtures.universalBait();
        useCase.addItem(bait, player, 2);
        useCase.removeItem(bait, player, 2);
        assertFalse(player.getInventory().contains(bait));
    }

    @Test
    void removeItem_insufficientQuantity_throws() {
        Bait bait = TestFixtures.universalBait();
        useCase.addItem(bait, player, 1);
        assertThrows(IllegalStateException.class,
                () -> useCase.removeItem(bait, player, 5));
    }

    @Test
    void removeItem_notInInventory_throws() {
        assertThrows(IllegalStateException.class,
                () -> useCase.removeItem(TestFixtures.universalBait(), player, 1));
    }

    // ── consumeBait ──────────────────────────────────────────────────────────

    @Test
    void consumeBait_removesOneUnit() {
        Bait bait = TestFixtures.universalBait();
        useCase.addItem(bait, player, 3);
        useCase.consumeBait(player, bait.getId());
        assertEquals(2, player.getInventory().getQuantity(bait));
    }

    @Test
    void consumeBait_returnsCorrectBait() {
        Bait bait = TestFixtures.universalBait();
        useCase.addItem(bait, player, 1);
        Bait result = useCase.consumeBait(player, bait.getId());
        assertEquals(bait.getId(), result.getId());
    }

    @Test
    void consumeBait_notABait_throws() {
        FishingRod rod = TestFixtures.defaultRod();
        useCase.addItem(rod, player, 1);
        assertThrows(IllegalArgumentException.class,
                () -> useCase.consumeBait(player, rod.getId()));
    }

    @Test
    void consumeBait_brokenBait_throws() {
        Bait bait = TestFixtures.universalBait();
        bait.wear(bait.getDurability()); // esaurisce la bait
        useCase.addItem(bait, player, 1);
        assertThrows(IllegalStateException.class,
                () -> useCase.consumeBait(player, bait.getId()));
    }

    @Test
    void consumeBait_notFound_throws() {
        assertThrows(IllegalStateException.class,
                () -> useCase.consumeBait(player, "id_inesistente"));
    }

    // ── getEquippedRod ───────────────────────────────────────────────────────

    @Test
    void getEquippedRod_returnsFirstWorkingRod() {
        FishingRod rod = TestFixtures.defaultRod();
        useCase.addItem(rod, player, 1);
        FishingRod result = useCase.getEquippedRod(player);
        assertEquals(rod.getId(), result.getId());
    }

    @Test
    void getEquippedRod_brokenRodPresent_returnsWorkingOne() {
        FishingRod broken = TestFixtures.fragileRod();
        broken.wear(broken.getDurability()); // rompi la canna
        FishingRod working = TestFixtures.defaultRod();

        useCase.addItem(broken, player,  1);
        useCase.addItem(working, player, 1);

        FishingRod result = useCase.getEquippedRod(player);
        assertEquals(working.getId(), result.getId());
    }

    @Test
    void getEquippedRod_noRodAvailable_throws() {
        useCase.addItem(TestFixtures.universalBait(), player, 3);
        assertThrows(IllegalStateException.class,
                () -> useCase.getEquippedRod(player));
    }

    @Test
    void getEquippedRod_onlyBrokenRods_throws() {
        FishingRod rod = TestFixtures.fragileRod();
        rod.wear(rod.getDurability());

        useCase.addItem(rod, player, 1);

        // Stampa di controllo temporanea per il debug:
        player.getInventory().getItemSet().forEach(i -> {
            if (i instanceof FishingRod r) {
                System.out.println("Durabilità nel player: " + r.getDurability() + " IsBroken: " + r.isBroken());
            }
        });

        assertThrows(IllegalStateException.class,
                () -> useCase.getEquippedRod(player));
    }


}