package ch.epfl.cs107.play.game.narpg;

import ch.epfl.cs107.play.Client;
import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.MovableNetworkEntity;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.*;
import ch.epfl.cs107.play.Server;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.narpg.actor.NetworkBomb;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.actor.projectiles.NetworkArrow;
import ch.epfl.cs107.play.game.narpg.actor.projectiles.NetworkMagic;
import ch.epfl.cs107.play.game.narpg.announcement.ServerAnnouncement;
import ch.epfl.cs107.play.game.narpg.areas.NetworkArena;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.window.Window;

import javax.net.ssl.SNIHostName;
import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.*;


public class NARPG extends AreaGame
{
    private List<NetworkARPGPlayer> players = new ArrayList<NetworkARPGPlayer>();
    private List<NetworkEntity> networkEntities = new ArrayList<>();
    //store items that couldn't be registered and register as soon as possible;
    private List<NetworkEntity> leftToRegister = new ArrayList<>();
    private Connection connection;
    private final boolean isServer;
    private String username;
    private NetworkARPGPlayer player;
    private ServerAnnouncement announcement;

    private double time =0;
    public NARPG(boolean isServer, Connection connection) {
        super();
        this.isServer = isServer;
        this.connection = connection;
        announcement = new ServerAnnouncement();
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
    public boolean begin( Window window, FileSystem fileSystem )
    {
        if ( super.begin( window, fileSystem ) ) {
            createAreas();
            Area area = setCurrentArea( "custom/Arena", true );
            getCurrentArea().registerActor( announcement );
            if ( !isServer ) {
                ((Client) connection).login();
                username = ((Client) connection).getUsername();
                DiscreteCoordinates spawnCoords = findRandomSpawn();
                player = new NetworkARPGPlayer( area, Orientation.DOWN, spawnCoords, connection, true,((Client)connection).getMainId(), username, 0 );
                new Packet04Chat(  username + " has connected" ).writeData( connection );
                area.registerActor( player );
                area.setViewCandidate( player );
                player.getSpawnPacket().writeData( connection );
                players.add( player );
                networkEntities.add( player );
            }
            return true;
        }
        return false;
    }

    private DiscreteCoordinates findRandomSpawn()
    {
        boolean canSpawnTo = false;
        DiscreteCoordinates coords;
        NetworkBomb dummy = new NetworkBomb( getCurrentArea(), Orientation.DOWN, new DiscreteCoordinates(1, 1 ), 0  );

        do {
            coords = new DiscreteCoordinates( getRandomPos(), getRandomPos() );
            canSpawnTo = getCurrentArea().canEnterAreaCells( dummy, Collections.singletonList( coords ) );
        } while ( !canSpawnTo );

        return coords;
    }

    private int getRandomPos()
    {
        // from 1 to 27
        return RandomGenerator.getInstance().nextInt( 26 ) + 1;
    }

    public void updateObject(Packet03Update update) {
        var entity = findEntity(update.getObjectId());
        entity.updateState(update.getUpdateMap());
    }

    public void moveObject(Packet02Move packet) {
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

    public void addChat( Packet04Chat packet )
    {
        announcement.addAnnouncement( packet.getText() );
    }

    public boolean spawnObject(Packet00Spawn packet) {
        NetworkEntities object = packet.getObject();
        Area area = getCurrentArea();
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
                        packet.getDiscreteCoordinate(), connection, false, packet.getInitialState());
                newPlayer.setId(packet.getObjectId());
                players.add(newPlayer);
                networkEntities.add(newPlayer);
                boolean registered = getCurrentArea().registerActor(newPlayer);
                if (!registered) leftToRegister.add(newPlayer);
                break;
            case BOMB:
                NetworkBomb newBomb = new NetworkBomb(area, packet.getOrientation(), packet.getDiscreteCoordinate(),packet.getInitialState());
                networkEntities.add( newBomb );
                area.registerActor( newBomb );
                break;
            case BOW:
                NetworkArrow newArrow = new NetworkArrow( area, packet.getOrientation(), packet.getDiscreteCoordinate(),connection,packet.getInitialState());
                networkEntities.add( newArrow );
                area.registerActor( newArrow );
                break;
            case STAFF:
                NetworkMagic newMagic = new NetworkMagic( area, packet.getOrientation(), packet.getDiscreteCoordinate(),connection, packet.getInitialState());
                networkEntities.add( newMagic );
                area.registerActor( newMagic );
        }

        return true;
    }

    public void login()
    {
        for ( NetworkEntity p : networkEntities ) {
            Packet00Spawn packet = p.getSpawnPacket();
            packet.writeData( connection );
        }
    }

    public void logout(Packet05Logout logoutPacket)
    {
        System.out.println(logoutPacket.getConnectionId());
        for (Iterator<NetworkARPGPlayer> iter = players.listIterator(); iter.hasNext();)
        {
            NetworkARPGPlayer p = iter.next();
            if(p.getId()==logoutPacket.getObjectId()) {
                getCurrentArea().unregisterActor(p);
                networkEntities.removeAll(Collections.singleton(p));
                iter.remove();
                if (isServer) {
                    System.out.println(p.isDead());
                    if(p.isDead() && p.getKiller()!=0){
                        new Packet04Chat(((NetworkARPGPlayer)findEntity(p.getKiller())).getUsername() + "  killed " +p.getUsername()).writeData(connection);
                    }
                    else{
                        new Packet04Chat(p.getUsername() + " has disconnected").writeData(connection);
                    }
                    logoutPacket.writeData(connection);
                }
            }
        }
    }

    @Override
    public void update( float deltaTime ) {
        time+=deltaTime;
        // register the entities that still need to be registered
        for (NetworkEntity e : leftToRegister)
        {
            if (getCurrentArea().registerActor(e)) {
                leftToRegister.remove(e);
                return;
            }
        }
        for( NetworkARPGPlayer p : players){
            if(p.isDead()&& p.isClientAuthority()){
                    getCurrentArea().end();
            }
        }

        if(time>.5d){
            //extremely useful for debugging
            //if(players.size()>0) System.out.println("p1 : " +((NetworkArena)getCurrentArea()).getBehavior().getEntityCount(players.get(0)));
            //if(players.size()>1) System.out.println("p2 : " +((NetworkArena)getCurrentArea()).getBehavior().getEntityCount(players.get(1)));
            time-=.5;
        }
        super.update(deltaTime);
    }
    public void removeEntitys(List<NetworkEntity> entitys){
        networkEntities.removeAll(entitys);
    }

    public NetworkEntity findEntity(int objectId) {
        for (NetworkEntity e : networkEntities) {
            if (e.getId() == objectId) return e;
        }
        return null;
    }

    @Override
    public void end()
    {
        if ( player == null || getCurrentArea() == null ) { return; }

        getWindow().dispose();
    }
    public int getClientPlayerId(){
        for(NetworkARPGPlayer p: players){
            if(p.isClientAuthority()) return p.getId();
        }
        throw new NoSuchElementException("no client player");
    }
    public int getClientPlayerId(long mainId){
        for(NetworkARPGPlayer p:players){
            if(p.getConnectionId()==mainId) return p.getId();
        }
        throw new NoSuchElementException("no client player found by mainId");
    }
}
