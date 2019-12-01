package ch.epfl.cs107.play.game.arpg.inventory;


public interface InventoryHolder {
    public boolean removeItem(ARPGItem item);
    public boolean addItem(ARPGItem item);
}
