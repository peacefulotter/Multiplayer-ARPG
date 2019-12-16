package ch.epfl.cs107.play.game.narpg.inventory.items;

import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.inventory.items.Heart;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.HashMap;

public class NetworkHeart extends Heart implements NetworkEntity
{
    /**
     * Default AreaEntity constructor
     *
     * @param area     (Area): Owner area. Not null
     * @param position (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public NetworkHeart( Area area, DiscreteCoordinates position )
    {
        super( area, position );
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

    @Override
    public void updateState(HashMap<String, String> updateMap) {

    }
}
