package ch.epfl.cs107.play.game.arpg.inventory;

import ch.epfl.cs107.play.game.rpg.inventory.InventoryItem;

public enum ARPGItems implements InventoryItem
{
    ARROW( "Arrow", "zelda/arrow.icon",1f, 10 ),
    BOMB( "Bomb", "zelda/bomb", 1f, 10 ),
    BOW( "Bow", "zelda/bow.icon", 1f, 10 ),
    CASTLEKEY( "CastleKey", "zelda/key", 0.1f, 10 ),
    STAFF( "Magic Wand", "zelda/staff_water.icon", 2f, 10 ),
    SWORD( "Sword", "zelda/sword.icon", 1f, 10 );

    protected String name;
    protected String spriteName;
    protected float weight;
    protected int price;


    ARPGItems( String name, String spriteName, float weight, int price )
    {
        this.name = name;
        this.spriteName = spriteName;
        this.weight = weight;
        this.price = price;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public float getWeight()
    {
        return weight;
    }

    @Override
    public float getPrice()
    {
        return price;
    }
}
