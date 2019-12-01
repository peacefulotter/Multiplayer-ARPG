package ch.epfl.cs107.play.game.arpg.inventory;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.inventory.Inventory;
import ch.epfl.cs107.play.game.rpg.inventory.InventoryItem;
import ch.epfl.cs107.play.math.Positionable;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public enum ARPGItem implements InventoryItem
{
    ARROW( "Arrow", "zelda/arrow.icon",1f, 10 ),
    BOMB( "Bomb", "zelda/bomb", 1f, 10 ),
    BOW( "Bow", "zelda/bow.icon", 1f, 10 ),
    CASTLEKEY( "CastleKey", "zelda/key", 0.1f, 10 ),
    STAFF( "Magic Wand", "zelda/staff_water.icon", 2f, 10 ),
    SWORD( "Sword", "zelda/sword.icon", 1f, 10 );

    private String name;
    private String spriteName;
    private float weight;
    private int price;
    private Sprite sprite;

    ARPGItem( String name, String spriteName, float weight, int price )
    {
        //sprite = new Sprite( spriteName, 2f, 2f, parent );
        this.spriteName = spriteName;
        this.weight = weight;
        this.price = price;
    }

    public void draw( Canvas canvas )
    {
        sprite.draw( canvas );
    }
}
