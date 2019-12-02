package ch.epfl.cs107.play.game.Inventory;

import ch.epfl.cs107.play.game.arpg.inventory.ARPGItem;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class Inventory
{
    private final float maxWeight;
    private float inventoryWeight;
    protected Hashtable<InventoryItem, Integer> inventory;
    private int currentIndex = 0;

    public Inventory( float maxWeight )
    {
        this.maxWeight = maxWeight;
        inventory= new Hashtable<InventoryItem, Integer>();
    }
    public boolean addItemToInventory(InventoryItem item){
        return addItemToInventory(item, 1);
    }

    public boolean addItemToInventory( InventoryItem item, Integer amount)
    {
        if ( item.getWeight()*amount + inventoryWeight <= maxWeight )
        {
            if(!inventory.containsKey(item)){
                inventory.put(item, amount);
            }else{
                int existingAmount = inventory.get(item);
                inventory.replace(item, existingAmount+amount);
            }
            inventoryWeight += item.getWeight()*amount;
            return true;
        }
        else {
            return false;
        }
    }
    public boolean removeItemFromInventory(InventoryItem item){
       return removeItemFromInventory(item, 1);
    }
    public boolean removeItemFromInventory( InventoryItem item, int amount)
    {
        int existingAmount=inventory.get(item);
        if(amount > inventory.get(item)) {
            return false;
        }
        inventory.replace(item, existingAmount-amount);
        inventoryWeight -= item.getWeight()*amount;
        return true;
    }

    public boolean isItemInInventory( InventoryItem item )
    {
        if(!inventory.contains(item)) return false;
        return inventory.get(item)>0;
    }
}
