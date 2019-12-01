package ch.epfl.cs107.play.game.rpg.inventory;

import ch.epfl.cs107.play.game.actor.Actor;

import java.util.ArrayList;
import java.util.List;


public class Inventory
{
    private final float maxWeight;
    private float inventoryWeight;
    private List<InventoryItem> inventory;
    private int currentIndex = 0;

    public Inventory( float maxWeight )
    {
        this.maxWeight = maxWeight;
        inventory = new ArrayList<InventoryItem>();
    }

    public boolean addItemToInventory( InventoryItem item )
    {
        if ( item.getWeight() + inventoryWeight <= maxWeight )
        {
            inventory.add( item );
            inventoryWeight += item.getWeight();
            System.out.println( "Item has been added to your inventory" );
            return true;
        }
        else {
            System.out.println( "This item is too heavy to be carried, remove some items from your inventory before taking it" );
            return false;
        }
    }

    public boolean removeItemFromInventory( InventoryItem item )
    {
        for ( InventoryItem i : inventory )
        {
            if ( i.equals( item ) )
            {
                inventory.remove( item );
                inventoryWeight -= item.getWeight();
                System.out.println(item.getName() + " has been removed from your inventory");
                return true;
            }
        }
        return false;
    }

    private boolean isItemInInventory( InventoryItem item )
    {
        for ( InventoryItem i : inventory )
        {
            if ( i.equals( item  ) )
            {
                return true;
            }
        }
        return false;
    }

    public InventoryItem getNextItem()
    {
        currentIndex++;
        return inventory.get( currentIndex );
    }

}
