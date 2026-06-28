package it.unicam.cs.mpgc.rpg130669.domain.model.fish;

import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;

import java.util.Objects;

/**
 * Live instance of a fish on the map.
 * Wraps a FishTemplate (static data) and adds the runtime state:
 * current position, behavioral state, remaining hp and stamina.
 */
public class FishEntity {
    private Position position;
    private FishBehaviorState behaviorState;
    private final FishTemplate template;
    private int hp;
    private int stamina;

    public FishEntity(Position position, FishTemplate template){
        this.position       = Objects.requireNonNull(position, "la position non può essere null");
        this.template       = Objects.requireNonNull(template, "il template non può essere null");
        this.behaviorState  = FishBehaviorState.IDLE;
        this.hp             = template.baseHp();
        this.stamina        = template.baseStamina();
    }


    public boolean isDefeated(){return hp <= 0;}
    public boolean isExhausted(){return stamina <= 0;}

    // Present here and not in the engine because it responds to a query about the fish itself
    public boolean isOutOfRange(Position playerPos) {
        return position.distanceTo(playerPos) > template.behaviorProfile().reactionRange();
    }

    // getter - setter made with AI
    //-------------------------------------------------------------------------------------------------------------------------------------

    public Position getPosition()               {return position;}
    public FishBehaviorState getBehaviorState() {return behaviorState;}
    public FishTemplate getTemplate()           {return template;}
    public int getHp()                          {return hp;}
    public int getStamina()                     {return stamina;}
    public String getId()                       {return template.id();}

    public void setPosition(Position position) {
        this.position = Objects.requireNonNull(position, "position non può essere null");
    }

    public void setBehaviorState(FishBehaviorState state) {
        this.behaviorState = Objects.requireNonNull(state, "state non può essere null");
    }

    public void takeDamage(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Il danno non può essere negativo: " + amount);
        hp = Math.max(0, hp - amount);
    }

    public void loseStamina(int amount) {
        if (amount < 0) throw new IllegalArgumentException("La stamina non può essere negativa: " + amount);
        stamina = Math.max(0, stamina - amount);
    }


}
