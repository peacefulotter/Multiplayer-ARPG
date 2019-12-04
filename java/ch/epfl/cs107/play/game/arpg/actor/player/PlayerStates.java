package ch.epfl.cs107.play.game.arpg.actor.player;

public enum PlayerStates {
    IDLE(true),
    ATTACKING_SWORD(true),
    ATTACKING_BOW(true),
    ATTACKING_STAFF(true),
    TAKING_DAMAGE(false);
    private boolean vulnerable;
    PlayerStates(boolean vulnerable){
        this.vulnerable=vulnerable;
    }
}
