package ch.epfl.cs107.play.game.arpg.inventory;

import ch.epfl.cs107.play.game.Inventory.InventoryItem;
import ch.epfl.cs107.play.game.arpg.actor.monster.Vulnerabilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// todo : ADD ENCHANTMENTS ?
public enum ARPGItem implements InventoryItem
{
    ARROW(
            "Arrow", "zelda/arrow.icon",
            1f, 10,
            false, Vulnerabilities.LONG_RANGE ),
    BOMB(
            "Bomb", "zelda/bomb",
            1f, 10,
            true, Vulnerabilities.CLOSE_RANGE ),
    BOW(
            "Bow", "zelda/bow.icon",
            1f, 10,
            true ),
    CASTLE_KEY(
            "CastleKey", "zelda/key",
            0.1f, 10,
            false),
    STAFF( "Magic Wand", "zelda/staff_water.icon",
            2f, 10,
            true, Vulnerabilities.MAGIC, Vulnerabilities.LONG_RANGE ),
    SWORD(
            "Sword", "zelda/sword.icon",
            1f, 10,
            true, Vulnerabilities.CLOSE_RANGE );

    protected String name;
    protected String spriteName;
    protected float weight;
    protected int price;
    private boolean equipable;
    private List<Vulnerabilities> weaponType;

    ARPGItem( String name, String spriteName, float weight, int price, boolean equipable, Vulnerabilities ... weaponType )
    {
        this.name = name;
        this.spriteName = spriteName;
        this.weight = weight;
        this.price = price;
        this.equipable=equipable;
        this.weaponType = new ArrayList<>();
        Collections.addAll( this.weaponType, weaponType );
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
