package it.unicam.cs.mpgc.rpg130669.presentation.controller;

import it.unicam.cs.mpgc.rpg130669.application.usecase.GameSessionUseCase;
import it.unicam.cs.mpgc.rpg130669.application.usecase.InventoryUseCase;
import it.unicam.cs.mpgc.rpg130669.domain.model.combat.FishingSession;
import it.unicam.cs.mpgc.rpg130669.domain.model.combat.PlayerAction;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishEntity;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.Bait;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.Item;
import it.unicam.cs.mpgc.rpg130669.presentation.viewmodel.GameViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the combat panel.
 * Receives references from the GameController when opened.
 */
public class CombatController {

    @FXML private Label       fishNameLabel;
    @FXML private Label       fishHpLabel;
    @FXML private Label       fishStaminaLabel;
    @FXML private ProgressBar fishHpBar;
    @FXML private ProgressBar fishStaminaBar;
    @FXML private Label       distanceLabel;
    @FXML private Label       rodDurLabel;
    @FXML private Label       turnLabel;
    @FXML private TextArea    combatLog;
    @FXML private Button      pullBtn;
    @FXML private Button      waitBtn;
    @FXML private Button      baitBtn;
    @FXML private Button      giveUpBtn;

    private GameSessionUseCase session;
    private InventoryUseCase   inventoryUseCase;
    private GameViewModel      viewModel;
    private FishingSession     fishingSession;
    private Runnable           onCombatEnd;

    public void init(GameSessionUseCase session,
                     InventoryUseCase inventoryUseCase,
                     GameViewModel viewModel,
                     FishingSession fishingSession,
                     Runnable onCombatEnd) {
        this.session          = session;
        this.inventoryUseCase = inventoryUseCase;
        this.viewModel        = viewModel;
        this.fishingSession   = fishingSession;
        this.onCombatEnd      = onCombatEnd;
        refresh();
    }

    /**
     * Refreshes the session and all percentage statistics.
     * Handles the next step of the player's turn, disabling the buttons.
     */
    private void refresh() {
        FishingSession fs = fishingSession;
        if (fs == null) return;

        FishEntity fish       = fs.getTargetFish();
        var        combat     = fs.getCombatState();
        int        maxHp      = fish.getTemplate().baseHp();
        int        maxStamina = fish.getTemplate().baseStamina();

        fishNameLabel.setText(fish.getTemplate().name()
                + "  [" + fish.getTemplate().rarity().getCode().toUpperCase() + "]");
        fishHpLabel.setText("HP: " + fish.getHp() + "/" + maxHp);
        fishStaminaLabel.setText("Stamina: " + fish.getStamina() + "/" + maxStamina);
        fishHpBar.setProgress((double) fish.getHp() / maxHp);
        fishStaminaBar.setProgress((double) fish.getStamina() / maxStamina);

        distanceLabel.setText("Distanza: " + combat.getVirtualDistance());
        rodDurLabel.setText("Canna: "    + combat.getRodDurability());
        turnLabel.setText("Turno: "      + combat.getTurnCount());

        combat.getActiveEffects().forEach(this::appendLog);

        boolean playerTurn = fs.isPlayerTurn();
        pullBtn.setDisable(!playerTurn);
        waitBtn.setDisable(!playerTurn);
        baitBtn.setDisable(!playerTurn);
        giveUpBtn.setDisable(fs.isConcluded());

        if (fs.isConcluded()) {
            handleConcluded(fs);
        }
    }

    /**
     * Creates the cases for the end of the session, covering all possible outcomes:
     * CAUGHT — you won the battle.
     * ESCAPED — the fish escaped.
     * GIVE UP — you surrendered.
     */
    private void handleConcluded(FishingSession fs) {
        String msg = switch (fs.getSessionState()) {
            case CAUGHT   -> "Pesce catturato!";
            case ESCAPED  -> "Il pesce è fuggito.";
            case GIVEN_UP -> "Hai abbandonato.";
            default       -> "";
        };
        appendLog("──────────────────");
        appendLog(msg);
        if (onCombatEnd != null) onCombatEnd.run();
    }

    @FXML private void onPull()   { performAction(PlayerAction.PULL,    null); }
    @FXML private void onWait()   { performAction(PlayerAction.WAIT,    null); }
    @FXML private void onGiveUp() { performAction(PlayerAction.GIVE_UP, null); }

    /**
     * Handles the use of BAIT; if used and present, consumes 1 unit of quantity.
     */
    @FXML
    private void onBait() {
        try {
            Bait bait = inventoryUseCase.consumeBait(
                    session.getPlayer(),
                    pickBaitId());
            performAction(PlayerAction.USE_BAIT, bait);
        } catch (IllegalStateException e) {
            appendLog(" attenzione " + e.getMessage());
        }
    }

    /**
     * @param action the combat action to perform
     * @param item the item to use
     * General method that calls the corresponding PerformCombatAction for any
     * item implementation and refreshes the session.
     */
    private void performAction(PlayerAction action, Item item) {
        try {
            session.performCombatAction(action, item);
            viewModel.refresh();
            refresh();
        } catch (Exception e) {
            appendLog("Errore: " + e.getMessage());
        }
    }

    /**
     * @return the ID of the first bait (which is the currently selected one).
     */
    private String pickBaitId() {
        return session.getPlayer().getInventory().getItemSet().stream()
                .filter(i -> i instanceof Bait)
                .findFirst()
                .map(it.unicam.cs.mpgc.rpg130669.domain.model.item.Item::getId)
                .orElseThrow(() -> new IllegalStateException("Nessuna esca nell'inventario"));
    }

    private void appendLog(String text) {
        combatLog.appendText(text + "\n");
    }
}
