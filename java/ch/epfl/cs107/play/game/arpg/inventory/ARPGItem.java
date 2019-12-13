package ch.epfl.cs107.play.game.arpg.inventory;

import ch.epfl.cs107.play.game.rpg.inventory.InventoryItem;
import ch.epfl.cs107.play.game.arpg.actor.monster.Vulnerabilities;


public enum ARPGItem implements InventoryItem
{
    ARROW(
            "Arrow", "zelda/arrow.icon",
            1f, 10,
            false, 1f, Vulnerabilities.LONG_RANGE ),
    BOMB(
            "Bomb", "zelda/bomb",
            1f, 10,
            true, 2f, Vulnerabilities.CLOSE_RANGE ),
    BOW(
            "Bow", "zelda/bow.icon",
            1f, 10,
            true, 0 ),
    CASTLE_KEY(
            "CastleKey", "zelda/key",
            0.1f, 10,
            false, 0 ),
    STAFF( "Magic Wand", "zelda/staff_water.icon",
            2f, 10,
            true, 1.5f, Vulnerabilities.MAGIC, Vulnerabilities.LONG_RANGE ),
    SWORD(
            "Sword", "zelda/sword.icon",
            1f, 10,
            true, 1, Vulnerabilities.CLOSE_RANGE );

    protected String name;
    protected String spriteName;
    protected float weight;
    protected int price;
    private boolean equippable;
    private float damage;
    private Vulnerabilities[] weaponType;

    ARPGItem( String name, String spriteName, float weight, int price, boolean equipable, float damage, Vulnerabilities ... weaponType )
    {
        this.name = name;
        this.spriteName = spriteName;
        this.weight = weight;
        this.price = price;
        this.equippable = equippable;
        this.damage = damage;
        this.weaponType = new Vulnerabilities[ weaponType.length ];
        for ( int i = 0; i < weaponType.length; i++ )
        {
            this.weaponType[ i ] = weaponType[ i ];
        }
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

    public boolean isEquippable() {
        return equippable;
    }

    public float getDamage() { return damage; }

    public Vulnerabilities[] getVuln() { return weaponType; }
}
