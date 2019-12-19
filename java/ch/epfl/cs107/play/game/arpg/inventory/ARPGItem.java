package ch.epfl.cs107.play.game.arpg.inventory;

import ch.epfl.cs107.play.game.rpg.inventory.InventoryItem;
import ch.epfl.cs107.play.game.arpg.actor.monster.Vulnerabilities;


public enum ARPGItem implements InventoryItem {
    ARROW(
            "Arrow", "zelda/arrow.icon",
            1f, 10,
            false, 1f, Vulnerabilities.LONG_RANGE),
    BOMB(
            "Bomb", "zelda/bomb",
            1f, 10,
            true, 2f, Vulnerabilities.CLOSE_RANGE),
    BOW(
            "Bow", "zelda/bow.icon",
            1f, 10,
            true, 0),
    CASTLE_KEY(
            "CastleKey", "zelda/key",
            0.1f, 10,
            true, 0),
    STAFF("Magic Wand", "zelda/staff_water.icon",
            2f, 10,
            true, 1.5f, Vulnerabilities.MAGIC),
    SWORD(
            "Sword", "zelda/sword.icon",
            1f, 10,
            true, 1, Vulnerabilities.CLOSE_RANGE);

    private final String name;
    private final String spriteName;
    private final float weight;
    private final int price;
    private final boolean equippable;
    private final float damage;
    private final Vulnerabilities weaponType;

    ARPGItem(String name, String spriteName, float weight, int price, boolean equippable, float damage) {
        this(name, spriteName, weight, price, equippable, damage, null);
    }

    ARPGItem(String name, String spriteName, float weight, int price, boolean equippable, float damage, Vulnerabilities weaponType) {
        this.name = name;
        this.spriteName = spriteName;
        this.weight = weight;
        this.price = price;
        this.equippable = equippable;
        this.damage = damage;
        this.weaponType = weaponType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public float getPrice() {
        return price;
    }

    @Override
    public String getSpriteName() {
        return spriteName;
    }

    public boolean isEquippable() {
        return equippable;
    }

    public float getDamage() {
        return damage;
    }

    public Vulnerabilities getVuln() {
        return weaponType;
    }
}
