package ch.epfl.cs107.play.game.narpg.inventory.items;

import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.inventory.items.Coin;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class NetworkCoin extends Coin implements NetworkEntity
{
    /**
     * Default AreaEntity constructor
     *
     * @param area     (Area): Owner area. Not null
     * @param position (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     * @param value
     */
    public NetworkCoin(Area area, DiscreteCoordinates position, int value)
    {
        super( area, position, value );
    }

    @Override
    public int getId()
    {
        return 0;
    }



    @Override
    public Packet00Spawn getSpawnPacket()
    {
        return null;
    }
}
