package ch.epfl.cs107.play.game.narpg.actor;

import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class NetworkGrass extends Grass implements NetworkEntity
{
    public NetworkGrass( Area area, Orientation orientation, DiscreteCoordinates position )
    {
        super(area, orientation, position);
    }

    @Override
    public int getId()
    {
        return NetworkEntities.GRASS.getClassId();
    }

    @Override
    public void setPosition(DiscreteCoordinates position)
    {

    }

    @Override
    public void setOrientation(Orientation orientation)
    {

    }

    @Override
    public Packet00Spawn getSpawnPacket()
    {
        return null;
    }
}
