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
            System.out.println( "Item has been added to your inventory" );
            return true;
        }
        else {
            System.out.println( "This item is too heavy to be carried, remove some items from your inventory before taking it" );
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
        System.out.println(item.getName() + " has been removed from your inventory");
        return true;
    }

    public boolean isItemInInventory( InventoryItem item )
    {
        if(!inventory.contains(item)) return false;
        return inventory.get(item)>0;
    }
}
