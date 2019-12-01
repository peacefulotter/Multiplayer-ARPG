package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.Inventory.InventoryItem;
import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.Inventory.Inventory;
import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGItem;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.lang.reflect.Array;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

public class ARPGInventory extends Inventory implements Actor {
    private int playerFortune = 0;
    private int playerMoney = 0;
    private Sprite sprite;
    private boolean isDisplaying = false;
    private AreaEntity holder;
    private InventoryItem[] itemOrder;
    private int inventorySize;
    private Integer itemOrderIndex;

    public ARPGInventory(AreaEntity holder, float maxWeight, int inventorySize) {
        super(maxWeight);
        this.holder = holder;
        sprite = new Sprite("zelda/inventory.background", 7f, 10f, this);
        itemOrder= new InventoryItem[inventorySize];
        itemOrderIndex = 0;
        this.inventorySize=inventorySize;

    }

    public void addMoney(int money) {
        playerMoney += money;
        playerFortune += money;
    }

    public int getMoney() {
        return playerMoney;
    }

    public int getFortune() {
        return playerFortune;
    }

    @Override
    public void draw(Canvas canvas) {
        if (isDisplaying) {
            sprite.draw(canvas);
        }
    }

    public void toggleDisplay() {
        System.out.println("toggle");
        isDisplaying = !isDisplaying;
    }


    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return null;
    }

    @Override
    public boolean addItemToInventory(InventoryItem item) {
        return addItemToInventory(item,1);
    }


    //adds item to inventory and adds it to first available slot in inventoryOrder
    //if added to inventory but no available slot found it removes it from inventory and returns false
    @Override
    public boolean addItemToInventory(InventoryItem item, Integer amount) {
        boolean existedBefore = isItemInInventory(item);
        boolean added = super.addItemToInventory(item, amount);
        boolean addedToOrder=true;
        if(added && !existedBefore){
            addedToOrder=false;
            for(int i=0; i< inventorySize;i++){
                itemOrder[i]=item;
                addedToOrder=true;
                break;
            }
        }
        if(added && !addedToOrder){
            removeItemFromInventory(item, amount);
            return false;
        }
        return added;
    }

    @Override
    public boolean removeItemFromInventory(InventoryItem item) {
        return removeItemFromInventory(item, 1);
    }

    @Override
    public boolean removeItemFromInventory(InventoryItem item, int amount) {
        boolean removed= super.removeItemFromInventory(item, amount);
        if(removed){
            if(inventory.get(item)==0){
               for(int i=0; i<inventorySize;i++){
                   if(item==itemOrder[i]){
                       itemOrder[i]=null;
                   }
               }
            }
        }
        return removed;
    }

    public InventoryItem getNextItem(int direction){
        itemOrderIndex+=direction;
        if(itemOrderIndex>=inventorySize){
            itemOrderIndex=0;
        }

        return itemOrder[itemOrderIndex];
    }


    public InventoryItem getCurrentItem() {
        return itemOrder[itemOrderIndex];
    }
}
