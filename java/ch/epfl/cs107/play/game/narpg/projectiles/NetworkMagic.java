package ch.epfl.cs107.play.game.narpg.projectiles;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.Networking.Packets.Packet03Update;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.projectiles.MagicProjectile;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.handler.NARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.HashMap;

public class NetworkMagic extends MagicProjectile implements NetworkEntity
{
    private final static float MAGIC_DAMAGE = 0.5f;
    private final Connection connection;
    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param speed
     * @param maxDistance
     */
    public NetworkMagic(Area area, Orientation orientation, DiscreteCoordinates position, int speed, int maxDistance, Connection connection )
    {
        super(area, orientation, position, speed, maxDistance);
        this.connection = connection;
        handler = new NetworkMagicHandler();
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


    @Override
    public void acceptInteraction( AreaInteractionVisitor v )
    {
        System.out.println("accept interaction magic projectile");
        ((NARPGInteractionVisitor) v).interactWith(this);
    }

    @Override
    public void interactWith(Interactable other)
    {
        System.out.println("interact with magic projectile");
        if ( connection.isServer() )
        {
            System.out.println("interact with only server");
            super.interactWith( other );
        }
    }

    class NetworkMagicHandler implements NARPGInteractionVisitor
    {
        @Override
        public void interactWith( NetworkARPGPlayer player )
        {
            // check server and send packet update
            System.out.println(player);
            player.giveDamage( MAGIC_DAMAGE );
            HashMap<String, String> updateMap = new HashMap<>();
            updateMap.put( "hp", String.valueOf(player.getHp()));
            Packet03Update packetUpdate = new Packet03Update( player.getId(), updateMap );
            packetUpdate.writeData( connection );
            System.out.println(player.getHp());
            stopProjectile();
        }
    }
}
