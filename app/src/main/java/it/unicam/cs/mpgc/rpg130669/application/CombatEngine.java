package it.unicam.cs.mpgc.rpg130669.application;


import it.unicam.cs.mpgc.rpg130669.domain.model.combat.CombatState;
import it.unicam.cs.mpgc.rpg130669.domain.model.combat.FishAction;
import it.unicam.cs.mpgc.rpg130669.domain.model.combat.FishingSession;
import it.unicam.cs.mpgc.rpg130669.domain.model.combat.PlayerAction;
import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishEntity;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.Bait;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.Item;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Stat;

import java.util.Random;

/**
 * Risolve i turni di una FishingSession.
 * Unica responsabilità: dato uno stato di sessione e un'azione,
 * produrre il nuovo stato. Non interagisce con persistenza né UI.
 */
public class CombatEngine {
    private static final int PULL_BASE_DAMAGE = 5;
    private static final int STRUGGLE_ROD_DAMAGE = 3;
    private static final int BITE_HARDER_ROD_DAMAGE = 5;
    private static final int SWIM_AWAY_DISTANCE = 2;
    private static final int TIRE_STAMINA_LOSS = 8;
    private static final int WAIT_STAMINA_LOSS = 3;

    private final Random random;

    public CombatEngine(Random random) {
        this.random = random;
    }

    // turno del giocatore

    public void applyPlayerAction(FishingSession session, PlayerAction action, Item usedItem) {

        if(!session.isPlayerTurn()) throw new IllegalStateException("non è il turno del giocatore");

        CombatState state = session.getCombatState();
        Player player = session.getPlayer();
        FishEntity fish = session.getTargetFish();
        state.clearEffects();

        switch (action) {
            case PULL -> resolvePull(state, fish, player);
            case USE_BAIT -> resolveBait(state, fish, usedItem);
            case WAIT -> resolveWait(state, fish, player);
            case GIVE_UP -> {
                session.setGivenUp();
                return;
            }
        }
        state.incrementTurn();
        checkOutcome(session);
        if (session.isActive()) session.toFishTurn();
    }

    private void resolvePull(CombatState state, FishEntity fish, Player player) {

        int damage = damageCalc(player, fish);

        fish.takeDamage(damage);
        state.decreaseDistance(1);
        state.addEffect("recuperi la lenza: " + damage + " danni al pesce");

        // esperienza guadagnata
        player.gainExp(Stat.STRENGTH, 5);
        player.gainExp(Stat.CASTING, 2);
    }

    private int damageCalc(Player p, FishEntity f) {
        // almeno 1 di danno garantito
        return Math.max(1, PULL_BASE_DAMAGE
                + p.getStat(Stat.STRENGTH)
                - f.getTemplate().combatStrength());
    }


    private void resolveBait(CombatState state, FishEntity fish, Item usedItem) {
        if (!(usedItem instanceof Bait bait)) {
            state.addEffect("l'item non è un esca");
            return;
        }
        float bonus = bait.effectiveBonus(fish.getTemplate().rarity());
        if (bonus <= 0f) {
            state.addEffect("l'esca " + bait.toString() + "non è efficace su questo pesce!");
            return;
        }
        int staminaDrain = (int) (bonus * 20);

        fish.loseStamina(staminaDrain);

        state.addEffect("Esca usata con successo il pesce perde " + staminaDrain + " punti stamina");
    }

    private void resolveWait(CombatState state, FishEntity fish, Player player) {
        int staminaDrain = WAIT_STAMINA_LOSS * player.getStat(Stat.PATIENCE);
        fish.loseStamina(staminaDrain);
        state.addEffect("Aspetti: il pesce perde:" + staminaDrain + " stamina");
        player.gainExp(Stat.PATIENCE, 8);
    }

    // ── turno pesce ───────────────────────────────────────────────────────────

    public void applyFishTurn(FishingSession session) {
        if (!session.isFishTurn())
            throw new IllegalStateException("Non è il turno del pesce");

        FishAction action = selectFishAction(session.getTargetFish());
        CombatState state = session.getCombatState();
        state.clearEffects();

        switch (action) {
            case STRUGGLE -> resolveStruggle(state);
            case SWIM_AWAY -> resolveSwimAway(state);
            case TIRE -> resolveTire(session.getTargetFish(), state);
            case BITE_HARDER -> resolveBiteHarder(state);
        }

        checkOutcome(session);
        if (session.isActive()) session.toPlayerTurn();
    }

    // selettore randomico, per azione del pesce
    private FishAction selectFishAction(FishEntity fish) {
        float fear = fish.getTemplate().behaviorProfile().fearThreshold();
        double roll = random.nextDouble();
        // Pesci con alta fearThreshold preferiscono SWIM_AWAY e STRUGGLE
        if (roll < fear * 0.4) return FishAction.SWIM_AWAY;
        if (roll < fear * 0.7) return FishAction.STRUGGLE;
        if (roll < fear * 0.7 + 0.15) return FishAction.BITE_HARDER;
        return FishAction.TIRE;
    }

    // resolver dei case:

    private void resolveStruggle(CombatState state) {
        state.damageRod(STRUGGLE_ROD_DAMAGE);
        state.addEffect("Il pesce si dibatte! Canna danneggiata di " + STRUGGLE_ROD_DAMAGE + ".");

    }

    private void resolveSwimAway(CombatState state) {
        state.increaseDistance(SWIM_AWAY_DISTANCE);
        state.addEffect("Il pesce nuota lontano! Distanza aumentata.");
    }

    private void resolveTire(FishEntity fish, CombatState state) {
        fish.loseStamina(TIRE_STAMINA_LOSS);
        state.addEffect("Il pesce si stanca da solo. Stamina -" + TIRE_STAMINA_LOSS + ".");
    }

    private void resolveBiteHarder(CombatState state) {
        state.damageRod(BITE_HARDER_ROD_DAMAGE);
        state.addEffect("Il pesce morde forte! Canna danneggiata di " + BITE_HARDER_ROD_DAMAGE + ".");
    }

    // ── esito ─────────────────────────────────────────────────────────────────

    private void checkOutcome(FishingSession session) {
        FishEntity fish = session.getTargetFish();
        CombatState state = session.getCombatState();

        if (fish.isDefeated() || fish.isExhausted()) {
            session.setCaught();
        } else if (state.isRodBroken() || state.isFishTooFar()) {
            session.setEscaped();
        }
    }

    private void NotifyStatGain(CombatState state, Player player,
                                Stat stat, int amount){
        int before = player.getStat(stat);
        player.gainExp(stat, amount);
        int after = player.getStat(stat);
        if (after > before) state.
                addEffect(stat.getCode().toUpperCase() + "è salita a: " + after + "!");
    }
}
