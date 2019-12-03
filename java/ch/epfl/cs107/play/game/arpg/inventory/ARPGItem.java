package ch.epfl.cs107.play.game.arpg.inventory;

import ch.epfl.cs107.play.game.Inventory.InventoryItem;

public enum ARPGItem implements InventoryItem
{
    ARROW( "Arrow", "zelda/arrow.icon",1f, 10, false),
    BOMB( "Bomb", "zelda/bomb", 1f, 10, true),
    BOW( "Bow", "zelda/bow.icon", 1f, 10,true ),
    CASTLE_KEY( "CastleKey", "zelda/key", 0.1f, 10, false),
    STAFF( "Magic Wand", "zelda/staff_water.icon", 2f, 10, true),
    SWORD( "Sword", "zelda/sword.icon", 1f, 10, true);

    protected String name;
    protected String spriteName;
    protected float weight;
    protected int price;
    private boolean equipable;

    ARPGItem(String name, String spriteName, float weight, int price, boolean equipable)
    {
        this.name = name;
        this.spriteName = spriteName;
        this.weight = weight;
        this.price = price;
        this.equipable=equipable;
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

    @Override
    public String getSpriteName(){
        return spriteName;
    }

    public boolean isEquipable() {
        return equipable;
    }
}
