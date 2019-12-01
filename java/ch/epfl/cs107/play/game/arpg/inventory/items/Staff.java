package ch.epfl.cs107.play.game.arpg.inventory.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGItem;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGItems;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Staff extends ARPGItem
{

    public Staff( Area area, DiscreteCoordinates coords )
    {
        super( ARPGItems.STAFF, area, Orientation.DOWN, coords );
    }

}
