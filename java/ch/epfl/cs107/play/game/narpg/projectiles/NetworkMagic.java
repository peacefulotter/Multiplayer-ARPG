package ch.epfl.cs107.play.game.narpg.projectiles;

import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.projectiles.MagicProjectile;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class NetworkMagic extends MagicProjectile implements NetworkEntity
{
    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param speed
     * @param maxDistance
     */
    public NetworkMagic(Area area, Orientation orientation, DiscreteCoordinates position, int speed, int maxDistance)
    {
        super(area, orientation, position, speed, maxDistance);
    }

    @Override
    public int getId()
    {
        return NetworkEntities.STAFF.getClassId();
    }


    @Override
    public Packet00Spawn getSpawnPacket()
    {
        return null;
    }
}
