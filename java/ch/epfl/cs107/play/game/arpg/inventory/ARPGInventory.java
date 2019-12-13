
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

    private AreaEntity holder;
    private InventoryItem[] itemOrder;

    private int inventorySize;
    private Integer itemOrderIndex;

    public ARPGInventory( AreaEntity holder, float maxWeight, int inventorySize, int initialCoins )
    {
        super( maxWeight );
        this.holder = holder;
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
        if ( added && !existedBefore )
        {
            playerFortune += item.getPrice();
            addedToOrder = false;
            for(int i=0; i< inventorySize;i++){
                if(itemOrder[i]==null){
                    itemOrder[i]=item;
                    addedToOrder=true;
                    break;
                }
            }
        }
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
            if ( inventory.get( item ) == 0 )
            {
                for ( int i = 0; i < inventorySize; i++ )
                {
                    if ( item == itemOrder[ i ] )
                    {
                        itemOrder[ i ] = null;
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
            if ( searchIndex >= inventorySize )
            {
                searchIndex -= inventorySize;
            }
            if ( searchIndex < 0 )
            {
                searchIndex += inventorySize;
            }
            if ( itemOrder[ searchIndex ] != null )
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
