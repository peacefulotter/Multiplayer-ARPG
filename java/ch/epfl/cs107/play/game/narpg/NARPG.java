package ch.epfl.cs107.play.game.narpg;

import ch.epfl.cs107.play.Client;
import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.MovableNetworkEntity;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.Networking.Packets.Packet02Move;
import ch.epfl.cs107.play.Networking.Packets.Packet03Update;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.actor.NetworkBomb;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.areas.NetworkArena;
import ch.epfl.cs107.play.game.narpg.projectiles.NetworkArrow;
import ch.epfl.cs107.play.game.narpg.projectiles.NetworkMagic;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class NARPG extends AreaGame
{
    private List<NetworkARPGPlayer> players = new ArrayList<NetworkARPGPlayer>();
    private List<NetworkEntity> networkEntities = new ArrayList<>();
    //store items that couldn't be registered and register as soon as possible;
    private List<NetworkEntity> leftToRegister = new ArrayList<>();
    private Connection connection;
    private boolean isServer;

    public NARPG(boolean isServer, Connection connection) {
        super();
        this.isServer = isServer;
        this.connection = connection;
    }

    @Override
    public String getTitle()
    {
        return "ZeldIC - Multiplayer";
    }

    protected void createAreas()
    {
        addArea( new NetworkArena( connection, isServer ) );
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            Area area = setCurrentArea("custom/Arena", true);
            if (!isServer) {
                ((Client) connection).login();
                var player = new NetworkARPGPlayer(area, Orientation.DOWN, new DiscreteCoordinates(6, 10), connection, true);
                area.registerActor( player );
                area.setViewCandidate( player );
                player.getSpawnPacket().writeData(connection);
                players.add(player);
                networkEntities.add(player);
            }
            return true;
        }
        return false;

    }

    public void updateObject(Packet03Update update) {
        var entity = findEntity(update.getObjectId());
        try {
            System.out.println(update.getBeanMap());
            BeanUtils.populate(entity, update.getBeanMap());
            System.out.println(entity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void moveObject(Packet02Move packet) {
        System.out.println(networkEntities.size());
        for (NetworkEntity p : networkEntities) {
            if (p.isMovable() && packet.getObjectId() == p.getId()) {
                if (p instanceof NetworkARPGPlayer) {
                    if (!((NetworkARPGPlayer) p).isClientAuthority()) {
                        ((MovableNetworkEntity) p).networkMove(packet);
                    }
                    return;
                }
                ((MovableNetworkEntity) p).networkMove(packet);

            }
        }
    }

    public boolean spawnObject(Packet00Spawn packet) {
        NetworkEntities object = packet.getObject();
        Area area = packet.getArea();
        System.out.println(packet.getObjectId());
        if (area == null) {
            area = getCurrentArea();
        }
        switch (object) {
            case PLAYER:
                for (NetworkARPGPlayer p : players)
                {
                    if (p.getId() == packet.getObjectId())
                    {
                        return false;
                    }
                }
                var newPlayer = new NetworkARPGPlayer(area, packet.getOrientation(),
                        packet.getDiscreteCoordinate(), connection, false);
                newPlayer.setId(packet.getObjectId());
                players.add(newPlayer);
                networkEntities.add(newPlayer);
                boolean registered = getCurrentArea().registerActor(newPlayer);
                System.out.println(registered);
                if (!registered) leftToRegister.add(newPlayer);
                break;
            case BOMB:
                NetworkBomb newBomb = new NetworkBomb(area, packet.getOrientation(), packet.getDiscreteCoordinate() );
                networkEntities.add( newBomb );
                area.registerActor( newBomb );
                break;
            case BOW:
                NetworkArrow newArrow = new NetworkArrow( area, packet.getOrientation(), packet.getDiscreteCoordinate(), 3, 8 );
                area.registerActor( newArrow );
                break;
            case STAFF:
                NetworkMagic newMagic = new NetworkMagic( area, packet.getOrientation(), packet.getDiscreteCoordinate(), 3, 8 );
                area.registerActor( newMagic );
        }

        return true;
    }

    public void login(long mainId) {
        for (NetworkEntity p : networkEntities) {
            var packet = p.getSpawnPacket();
            packet.writeData(connection);
        }
    }

    @Override
    public void update( float deltaTime )
    {
        super.update( deltaTime );
        for ( NetworkEntity e : leftToRegister )
        {
            if (getCurrentArea().registerActor(e)) {
                leftToRegister.remove(e);
                return;
            }
        }
    }

    public NetworkEntity findEntity(int objectId) {
        for (NetworkEntity e : networkEntities) {
            if (e.getId() == objectId) return e;
        }
        return null;
    }
}
