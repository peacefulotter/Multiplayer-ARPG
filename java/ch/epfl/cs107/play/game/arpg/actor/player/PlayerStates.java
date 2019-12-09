package ch.epfl.cs107.play.game.arpg.actor.player;

public enum PlayerStates {
    IDLE(true, false ),
    ATTACKING_SWORD(true, true ),
    ATTACKING_BOW(true, false ),
    ATTACKING_STAFF(true, false ),
    TAKING_DAMAGE(false, false ),
    IS_DASHING( true, false );

    private boolean vulnerable;
    private boolean closeRangeAttack;

    PlayerStates( boolean vulnerable, boolean closeRangeAttack )
    {
        this.vulnerable = vulnerable;
        this.closeRangeAttack = closeRangeAttack;
    }

    public boolean isCloseRangeAttacking()
    {
        return closeRangeAttack;
    }
}
