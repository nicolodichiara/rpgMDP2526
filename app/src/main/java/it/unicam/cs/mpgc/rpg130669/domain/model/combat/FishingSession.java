package it.unicam.cs.mpgc.rpg130669.domain.model.combat;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.FishEntity;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.FishingRod;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;

import java.util.Objects;

    /**
     * Sessione di pesca attiva tra un giocatore e un pesce target.
     * Gestisce il flusso dei turni e lo stato della sessione.
     * La logica di risoluzione delle azioni è delegata al CombatEngine
     * nel layer application — questa classe è solo il modello dati.
     */
    public class FishingSession {

        private final Player       player;
        private final FishEntity   targetFish;
        private final CombatState  combatState;
        private       SessionState sessionState;

        public FishingSession(Player player, FishEntity targetFish, FishingRod rod) {
            this.player       = Objects.requireNonNull(player,     "player non può essere null");
            this.targetFish   = Objects.requireNonNull(targetFish, "targetFish non può essere null");
            Objects.requireNonNull(rod, "rod non può essere null");
            this.combatState  = new CombatState(rod.getDurability());
            this.sessionState = SessionState.CASTING;
        }

        public boolean isActive() {
            return sessionState == SessionState.PLAYER_TURN
                    || sessionState == SessionState.FISH_TURN
                    || sessionState == SessionState.CASTING
                    || sessionState == SessionState.FISH_SPOTTED;
        }

        public boolean isConcluded() {
            return sessionState == SessionState.CAUGHT
                    || sessionState == SessionState.ESCAPED
                    || sessionState == SessionState.GIVEN_UP;
        }

        //parte fatta con AI

        public Player       getPlayer()       { return player;       }
        public FishEntity   getTargetFish()   { return targetFish;   }
        public CombatState  getCombatState()  { return combatState;  }
        public SessionState getSessionState() { return sessionState; }

        public void startCombat()  { sessionState = SessionState.PLAYER_TURN; }
        public void toFishTurn()   { sessionState = SessionState.FISH_TURN;   }
        public void toPlayerTurn() { sessionState = SessionState.PLAYER_TURN; }
        public void setCaught()    { sessionState = SessionState.CAUGHT;      }
        public void setEscaped()   { sessionState = SessionState.ESCAPED;     }
        public void setGivenUp()   { sessionState = SessionState.GIVEN_UP;    }

        public boolean isPlayerTurn() { return sessionState == SessionState.PLAYER_TURN; }
        public boolean isFishTurn()   { return sessionState == SessionState.FISH_TURN;   }


    }

