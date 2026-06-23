package it.unicam.cs.mpgc.rpg130669.util;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.*;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.*;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.*;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.*;

import java.util.List;

/** Factory methods condivisi tra tutti i test. */
public final class TestFixtures {
    private TestFixtures() {}

    // ── GameMap ───────────────────────────────────────────────────────────────

    /** Mappa NxN completamente in acqua. */
    public static GameMap allWaterMap(int size) {
        Tile water = new Tile(TileType.WATER);
        TileGrid grid = new TileGrid(size, size, water);
        return new GameMap(1, "Test Map", grid, List.of(), 1);
    }

    /** Mappa con bordo GRASS e interno WATER. */
    public static GameMap mixedMap(int rows, int cols) {
        Tile water = new Tile(TileType.WATER);
        Tile grass = new Tile(TileType.GRASS);
        Tile[][] tiles = new Tile[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                tiles[r][c] = (r == 0 || r == rows - 1 || c == 0 || c == cols - 1)
                        ? grass : water;
        return new GameMap(1, "Mixed Map", new TileGrid(tiles), List.of(), 1);
    }
    // CODICE AI
    // ── BehaviorProfile ───────────────────────────────────────────────────────

    public static BehaviorProfile defaultProfile() {
        return new BehaviorProfile(0.5f, 0.5f, 0.3f, 5, 1);
    }

    /** Alta curiosità, bassa paura — si avvicina facilmente. */
    public static BehaviorProfile curiousProfile() {
        return new BehaviorProfile(0.99f, 0.01f, 0.3f, 5, 1);
    }

    /** Alta paura, bassa curiosità — fugge facilmente. */
    public static BehaviorProfile scaredProfile() {
        return new BehaviorProfile(0.01f, 0.99f, 0.3f, 5, 1);
    }

    // ── FishTemplate ──────────────────────────────────────────────────────────

    public static FishTemplate defaultTemplate() {
        return new FishTemplate("fish_001", "Trota", FishRarity.COMMON,
                defaultProfile(), 20, 20, 3, 5);
    }

    /** HP e stamina = 1: un singolo PULL lo cattura. */
    public static FishTemplate weakTemplate() {
        return new FishTemplate("fish_weak", "Pesciolino", FishRarity.COMMON,
                defaultProfile(), 1, 1, 1, 0);
    }

    public static FishTemplate rareTemplate() {
        return new FishTemplate("fish_rare", "Salmone", FishRarity.RARE,
                defaultProfile(), 30, 30, 6, 10);
    }

    // ── FishEntity ────────────────────────────────────────────────────────────

    public static FishEntity fishAt(Position pos) {
        return new FishEntity(pos, defaultTemplate());
    }

    public static FishEntity weakFishAt(Position pos) {
        return new FishEntity(pos, weakTemplate());
    }

    // ── Player ────────────────────────────────────────────────────────────────

    public static Player playerAt(Position pos) {
        return new Player("player_001", "TestPlayer", pos);
    }

    // ── Item ─────────────────────────────────────────────────────────────────

    public static FishingRod defaultRod() {
        return new FishingRod("rod_001", "Canna Base", "desc", 50, 5, 3);
    }

    /** Durabilità 3: un STRUGGLE (3 danni) la rompe. */
    public static FishingRod fragileRod() {
        return new FishingRod("rod_fragile", "Canna Fragile", "desc", 3, 3, 3);
    }

    public static Bait universalBait() {
        return new Bait("bait_001", "Verme", "desc", 5, 0.5f, null);
    }

    public static Bait rareBait() {
        return new Bait("bait_rare", "Esca Rara", "desc", 5, 0.9f, FishRarity.RARE);
    }

    public static Accessory defaultAccessory() {
        return new Accessory("acc_001", "Cappello", "desc",
                List.of(new StatModifier(Stat.PERCEPTION, 2)));
    }


}
