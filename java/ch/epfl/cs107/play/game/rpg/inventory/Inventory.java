package ch.epfl.cs107.play.game.rpg.inventory;

import java.util.Hashtable;


public class Inventory
{
    private final float maxWeight;
    private float inventoryWeight;
    protected Hashtable<InventoryItem, Integer> inventory;
    private int currentIndex = 0;

    public Inventory( float maxWeight )
    {
        this.maxWeight = maxWeight;
        inventory = new Hashtable<InventoryItem, Integer>();
    }

    protected boolean addItemToInventory( InventoryItem item ){
        return addItemToInventory(item, 1);
    }

    protected boolean addItemToInventory( InventoryItem item, Integer amount )
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

    protected boolean removeItemFromInventory( InventoryItem item ){
       return removeItemFromInventory(item, 1);
    }

    protected boolean removeItemFromInventory( InventoryItem item, int amount )
    {
        if(!inventory.containsKey(item)) return false;
        int existingAmount = inventory.get( item );
        if ( amount > inventory.get( item ) )
        {
            return false;
        }
        inventory.replace(item, existingAmount-amount );
        inventoryWeight -= item.getWeight() * amount;
        return true;
    }

    protected boolean isItemInInventory( InventoryItem item )
    {
        if ( !inventory.contains( item ) ) { return false; }
        return inventory.get( item ) > 0;
    }

    public int getItem( InventoryItem item )
    {
        if ( inventory.containsKey( item ) )
        {
            return inventory.get( item );
        }
        else
        {
            return 0;
        }
    }
}
