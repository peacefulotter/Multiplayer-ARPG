package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.window.Keyboard;


public enum PlayerInput {
    INTERACT(Keyboard.E, false),
    SHOW_INV(Keyboard.I, false),
    NEXT_ITEM(Keyboard.TAB, false),
    USE_ITEM(Keyboard.SPACE, true),
    DASH(Keyboard.A,false);

    private int keyCode;
    private boolean canHoldDown;

    PlayerInput(int keyCode, boolean canHoldDown){
        this.keyCode=keyCode;
        this.canHoldDown=canHoldDown;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public boolean getCanHoldDown() {
        return canHoldDown;
    }
}
