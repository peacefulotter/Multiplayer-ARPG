package ch.epfl.cs107.play.game.arpg.inventory;


import ch.epfl.cs107.play.game.rpg.inventory.Inventory;
import ch.epfl.cs107.play.game.rpg.inventory.InventoryItem;

public interface InventoryHolder {
    public boolean possess( InventoryItem item );
    public boolean removeItem( InventoryItem item );
    public boolean addItem( InventoryItem item );
}
