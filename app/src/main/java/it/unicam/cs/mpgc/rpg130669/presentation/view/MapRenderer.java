package it.unicam.cs.mpgc.rpg130669.presentation.view;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishBehaviorState;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishVisibility;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import it.unicam.cs.mpgc.rpg130669.presentation.viewmodel.TileViewModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Map;

/**
 * Canvas che disegna la mappa tile per tile.
 * Scala automaticamente TILE_SIZE alla dimensione del canvas.
 *
 * Stile visivo ispirato a Stardew Valley:
 * - palette desaturata con accenti vivaci
 * - bordo scuro sottile tra le tile (pixel art look)
 * - entità disegnate come forme geometriche colorate (placeholder sprite)
 */
public class MapRenderer extends Canvas {

    private static final int TILE_SIZE  = 48;   // pixel per tile
    private static final int BORDER     = 1;    // pixel di bordo tra tile

    // Colori entità
    private static final Color PLAYER_COLOR   = Color.web("#f5e642");
    private static final Color PLAYER_OUTLINE = Color.web("#2c1a00");

    // Colori pesce per visibilità
    private static final Color FISH_VISIBLE    = Color.web("#ff6b35");
    private static final Color FISH_SILHOUETTE = Color.web("#cc5500", 0.6);
    private static final Color FISH_SHADOW     = Color.web("#1a3a5c", 0.4);
    // HIDDEN: non disegnato

    // Colore overlay stato comportamentale
    private static final Color ATTRACTED_OVERLAY = Color.web("#ffff00", 0.25);
    private static final Color FLEEING_OVERLAY   = Color.web("#ff0000", 0.20);

    private Map<Position, TileViewModel> currentGrid;
    private int rows = 0;
    private int cols = 0;

    public MapRenderer() {
        super(0, 0);
    }

    /**
     * Aggiorna la griglia e ridisegna tutto il canvas.
     * Chiamato da GameController ogni volta che GameViewModel notifica un cambio.
     */
    public void render(Map<Position, TileViewModel> grid, int rows, int cols) {
        this.currentGrid = grid;
        this.rows        = rows;
        this.cols        = cols;

        setWidth (cols * TILE_SIZE);
        setHeight(rows * TILE_SIZE);

        draw();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (currentGrid == null) return;

        for (Map.Entry<Position, TileViewModel> entry : currentGrid.entrySet()) {
            Position      pos = entry.getKey();
            TileViewModel vm  = entry.getValue();
            drawTile(gc, pos, vm);
        }
    }

    private void drawTile(GraphicsContext gc, Position pos, TileViewModel vm) {
        double x = pos.col() * TILE_SIZE;
        double y = pos.row() * TILE_SIZE;
        double s = TILE_SIZE - BORDER;

        // 1 — sfondo tile
        Color baseColor = TileSprite.of(vm.tileType()).getColor();
        gc.setFill(baseColor);
        gc.fillRect(x, y, s, s);

        // 2 — overlay stato pesce (visible anche senza il pesce disegnato)
        if (vm.hasFish() && vm.fishState() != null)
            drawBehaviorOverlay(gc, x, y, s, vm.fishState());

        // 3 — sprite pesce (dipende da visibilità)
        if (vm.hasFish() && vm.fishVisibility() != null
                && vm.fishVisibility() != FishVisibility.HIDDEN)
            drawFish(gc, x, y, s, vm.fishVisibility());

        // 4 — sprite giocatore
        if (vm.hasPlayer())
            drawPlayer(gc, x, y, s);

        // 5 — bordo scuro pixel-art
        gc.setStroke(Color.web("#1a1a1a", 0.3));
        gc.setLineWidth(BORDER);
        gc.strokeRect(x, y, s, s);
    }

    private void drawBehaviorOverlay(GraphicsContext gc, double x, double y,
                                     double s, FishBehaviorState state) {
        Color overlay = switch (state) {
            case ATTRACTED          -> ATTRACTED_OVERLAY;
            case SCARED, FLEEING    -> FLEEING_OVERLAY;
            default                 -> null;
        };
        if (overlay != null) {
            gc.setFill(overlay);
            gc.fillRect(x, y, s, s);
        }
    }

    private void drawFish(GraphicsContext gc, double x, double y,
                          double s, FishVisibility visibility) {
        Color fishColor = switch (visibility) {
            case CLEAR    -> FISH_VISIBLE;
            case SILHOUETTE -> FISH_SILHOUETTE;
            case SHADOW     -> FISH_SHADOW;
            case HIDDEN     -> null;
        };
        if (fishColor == null) return;

        // Pesce disegnato come ellisse orizzontale centrata nella tile
        double fw = s * 0.5;
        double fh = s * 0.28;
        double fx = x + (s - fw) / 2.0;
        double fy = y + (s - fh) / 2.0;
        gc.setFill(fishColor);
        gc.fillOval(fx, fy, fw, fh);

        // Pinna caudale — triangolino a sinistra dell'ellisse
        if (visibility == FishVisibility.CLEAR) {
            gc.fillPolygon(
                    new double[]{fx, fx - s * 0.12, fx - s * 0.12},
                    new double[]{fy + fh / 2, fy + fh * 0.1, fy + fh * 0.9},
                    3
            );
        }
    }

    private void drawPlayer(GraphicsContext gc, double x, double y, double s) {
        // Corpo — cerchio giallo
        double r  = s * 0.28;
        double cx = x + s / 2.0;
        double cy = y + s * 0.42;
        gc.setFill(PLAYER_COLOR);
        gc.fillOval(cx - r, cy - r, r * 2, r * 2);

        // Outline scuro pixel-art
        gc.setStroke(PLAYER_OUTLINE);
        gc.setLineWidth(1.5);
        gc.strokeOval(cx - r, cy - r, r * 2, r * 2);

        // Puntino canna da pesca — linea sottile verso il basso-destra
        gc.setStroke(Color.web("#8B6914"));
        gc.setLineWidth(1.5);
        gc.strokeLine(cx + r * 0.6, cy + r * 0.6, cx + r * 1.8, cy + r * 2.2);

        // Etichetta "P" (rimosso quando ci sono gli sprite veri)
        gc.setFill(PLAYER_OUTLINE);
        gc.setFont(Font.font("Monospace", 10));
        gc.fillText("P", cx - 3, cy + 4);
    }

    public int getTileSize() { return TILE_SIZE; }
}
