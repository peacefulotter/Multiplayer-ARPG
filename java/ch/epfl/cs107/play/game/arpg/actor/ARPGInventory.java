package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.inventory.Inventory;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class ARPGInventory extends Inventory implements Actor
{
    private int playerFortune = 0;
    private int playerMoney = 0;
    private Sprite sprite;
    private boolean isDisplaying = false;

    public ARPGInventory( float maxWeight )
    {
        super( maxWeight );
        sprite = new Sprite( "zelda/inventory.background", 7f, 10f, this );
    }

    public void addMoney( int money )
    {
        playerMoney += money;
        playerFortune += money;
    }

    public int getMoney() { return playerMoney; }

    public int getFortune() { return playerFortune; }

    @Override
    public void draw( Canvas canvas )
    {
        if ( isDisplaying )
        {
            sprite.draw( canvas );
        }
    }

    public void toggleDisplay()
    {
        isDisplaying = !isDisplaying;
    }



    @Override
    public Transform getTransform()
    {
        return null;
    }

    @Override
    public Vector getVelocity()
    {
        return null;
    }
}
