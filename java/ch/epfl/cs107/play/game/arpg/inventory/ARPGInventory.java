
package ch.epfl.cs107.play.game.arpg.inventory;

import ch.epfl.cs107.play.game.rpg.inventory.InventoryItem;
import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.inventory.Inventory;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class ARPGInventory extends Inventory implements Actor
{
    private int playerFortune;
    private int playerMoney;

    private Sprite sprite;
    private boolean isDisplaying = false;

    //item order contains the order in which the items will be displayed on the player GUI
    private InventoryItem[] itemOrder;

    private int inventorySize;
    private Integer itemOrderIndex;

    //No inventory holder as we used the inventory on only the ARPGPlayer and NetworkARPGLPlayer
    public ARPGInventory( float maxWeight, int inventorySize, int initialCoins )
    {
        super( maxWeight );
        playerFortune = initialCoins;
        playerMoney = initialCoins;
        sprite = new Sprite( "zelda/inventory.background", 7f, 10f, this );
        itemOrder = new InventoryItem[ inventorySize ];
        itemOrderIndex = 0;
        this.inventorySize = inventorySize;

    }

    public void addMoney( int money )
    {
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
    public void draw( Canvas canvas )
    {
        if ( isDisplaying ) {
            sprite.draw( canvas );
        }
    }

    public void toggleDisplay() {
        isDisplaying = !isDisplaying;
    }

    @Override
    public boolean addItemToInventory( InventoryItem item ) {
        return addItemToInventory( item,1 );
    }

    //adds item to inventory and adds it to first available slot in inventoryOrder
    //if added to inventory but no available slot found it removes it from inventory and returns false
    @Override
    public boolean addItemToInventory( InventoryItem item, Integer amount )
    {
        boolean existedBefore = isItemInInventory(item);
        boolean added = super.addItemToInventory(item, amount);
        boolean addedToOrder = true;
        //makes sure that we only try to add the item to the item order if we successfully added it to the inventory hashMap
        if ( added && !existedBefore )
        {
            playerFortune += item.getPrice();
            addedToOrder = false;
            //adds item to first available slot in order
            for(int i=0; i< inventorySize;i++){
                if(itemOrder[i]==null){
                    itemOrder[i]=item;
                    addedToOrder=true;
                    break;
                }
            }
        }
        //makes sure there was space in the order
        if(added && !addedToOrder){
            removeItemFromInventory(item, amount);
            return false;
        }
        return added;
    }

    @Override
    public boolean removeItemFromInventory( InventoryItem item )
    {
        return removeItemFromInventory( item, 1 );
    }

    @Override
    public boolean removeItemFromInventory( InventoryItem item, int amount )
    {
        boolean removed = super.removeItemFromInventory( item, amount );
        if ( removed )
        {
            playerFortune -= item.getPrice();
            //if no items in inventory we need to remove it from the item order
            if ( inventory.get( item ) == 0 )
            {
                for ( int i = 0; i < inventorySize; i++ )
                {
                    if ( item == itemOrder[ i ] )
                    {
                        itemOrder[ i ] = null;
                        //sets the current item that the player is holding to the next one to avoid empty space in hand
                        getNextItem( 1 );
                    }
                }
            }
        }
        return removed;
    }

    public InventoryItem getNextItem( int direction )
    {
        int searchIndex = itemOrderIndex;
        for ( int i = 0; i < inventorySize; i++ )
        {
            searchIndex += direction;
            //makes sure that the searchIndex loops back to the start of the item order when searching
            if ( searchIndex >= inventorySize )
            {
                searchIndex -= inventorySize;
            }
            //makes sure that the searchIndex loops back to the end of the item order when searching
            if ( searchIndex < 0 )
            {
                searchIndex += inventorySize;
            }
            if ( itemOrder[ searchIndex ] != null && ((ARPGItem)itemOrder[searchIndex]).isEquippable() )
            {
                itemOrderIndex = searchIndex;
                break;
            }
        }

        return itemOrder[ itemOrderIndex ];
    }


    public InventoryItem getCurrentItem()
    {
        return itemOrder[ itemOrderIndex ];
    }

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return null;
    }
}
