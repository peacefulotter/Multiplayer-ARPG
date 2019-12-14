package ch.epfl.cs107.play.game.narpg;

import ch.epfl.cs107.play.Client;
import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.MovableNetworkEntity;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.*;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.actor.NetworkedBomb;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.areas.NFerme;
import ch.epfl.cs107.play.game.rpg.RPG;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;


public class NARPG extends AreaGame
{
    private List<NetworkARPGPlayer> players = new ArrayList<NetworkARPGPlayer>();
    private List<NetworkEntity> networkEntities = new ArrayList<>();
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
        addArea(new NFerme());
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            Area area = setCurrentArea("zelda/Ferme", true);
            if(!isServer){
                ((Client) connection).login();
                var player = new NetworkARPGPlayer(area, Orientation.DOWN, new DiscreteCoordinates(6, 10), connection,true);
                //initPlayer(player);
                getCurrentArea().registerActor(player);
                getCurrentArea().setViewCandidate(player);
                player.getSpawnPacket().writeData( connection );
                players.add(player);
                networkEntities.add(player);
            }
            return true;
        }
        return false;

    }

    public void updateState(Packet03Update update) {
        var entity= findEntity(update.getObjectId());
        try {
            System.out.println(update.getBeanMap());
            BeanUtils.populate(entity,update.getBeanMap());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void moveObject(Packet02Move packet) {
        for (NetworkEntity p : networkEntities) {
            if (p.isMovable() && packet.getObjectId() == p.getId()) {
                if (p instanceof NetworkARPGPlayer) {
                    if (!((NetworkARPGPlayer) p).isClientAuthority()) {
                        ((MovableNetworkEntity) p).networkMove(packet.getOrientation(), packet.getSpeed(), packet.getStart());
                    }
                    return;
                }
                ((MovableNetworkEntity) p).networkMove(packet.getOrientation(), packet.getSpeed(), packet.getStart());

            }
        }
    }

    public boolean spawnObject( Packet00Spawn packet ) {
        System.out.println("spawnobject in NARPG");
        NetworkEntities object = packet.getObject();
        Area area = packet.getArea();
        if ( area == null ) { area = getCurrentArea(); }
        switch ( object )
        {
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
                area.registerActor(newPlayer);
                break;
            case BOMB:
                NetworkedBomb newBomb = new NetworkedBomb(area, packet.getOrientation(), packet.getDiscreteCoordinate(),
                        connection);
                networkEntities.add( newBomb );
                area.registerActor( newBomb );
                break;
        }
        return  true;
    }

    public void login(long mainId){
        for(NetworkEntity p: networkEntities){
            var packet = p.getSpawnPacket();
            packet.writeData(connection);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
    public NetworkEntity findEntity(int objectId){
        for(NetworkEntity e: networkEntities){
            if(e.getId()==objectId) return e;
        }
        return null;
    }
}
