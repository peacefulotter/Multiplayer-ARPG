package ch.epfl.cs107.play.game.Inventory;


public interface InventoryItem
{

    String getName();
    float getWeight();
    float getPrice();
    String getSpriteName();

    default boolean equals( InventoryItem other )
    {
        System.out.println("compared two items");
        // might add some other conditions to make sure it is the same object
        // id ?
        return other.getName().equals( getName() ) &&
                other.getPrice() == getPrice() &&
                other.getWeight() == getWeight();
    }

}
