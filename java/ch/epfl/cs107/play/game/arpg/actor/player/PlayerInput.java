package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.window.Keyboard;


public enum PlayerInput {
    INTERACT(Keyboard.E),
    SHOW_INV(Keyboard.I),
    NEXT_ITEM(Keyboard.TAB),
    USE_ITEM(Keyboard.SPACE);
    private int keyCode;

    PlayerInput(int keyCode){
        this.keyCode=keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
