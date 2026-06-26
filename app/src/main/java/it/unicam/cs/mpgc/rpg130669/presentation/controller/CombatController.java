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
 * Controller del pannello di combattimento.
 * Riceve i riferimenti dal GameController quando viene aperto.
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
     * fa il refresh della sessione, di tutte le statistiche percentuali
     * gestisce il passo successivo del turno del giocatore, disabilitando i bottoni
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
     * crea i case per la conclusione della sessione con tutti i casi possibili
     * CAUGHT,  hai vinto il combattimento
     * ESCAPED, il pesce è fuggito
     * GIVE UP, ti sei arreso
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
     * gestisce l'uso della BAIT, se usata e presente ne consuma 1 qta
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
     *
     * @param action
     * @param item
     * metodo generale che per ogni implementazione di item,
     * chiama la PerformCombatAction relativa e refresha la sessione
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
     * @return l'id della prima esca (quindi quella selezionata).
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
