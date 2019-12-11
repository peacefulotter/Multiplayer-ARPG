package ch.epfl.cs107.play.game.narpg;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.MovableNetworkEntity;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.Networking.Packets.Packet02Move;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.actor.NetworkedBomb;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;


public class NARPG extends ARPG {
    private List<NetworkARPGPlayer> players = new ArrayList<NetworkARPGPlayer>();
    private List<NetworkEntity> networkEntities = new ArrayList<>();
    private Connection connection;
    private boolean isServer;

    public NARPG(boolean isServer,Connection connection) {
        super();
        this.isServer = isServer;
        this.connection=connection;
    }


    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            Area area = setCurrentArea("zelda/Ferme", true);
            if(!isServer){
                var player = new NetworkARPGPlayer(getCurrentArea(), Orientation.DOWN, new DiscreteCoordinates(6, 10), connection,true);
                initPlayer(player);
                new Packet00Spawn(player.getId(), NetworkEntities.PLAYER, player.getOrientation(),player.getCurrentCells().get(0),getCurrentArea()).writeData(connection);
                players.add(player);
                networkEntities.add(player);
            }
            return true;
        }
        return false;

    }


    public void updateState(Packet packet) {
        System.out.println(packet.getObjectId());
    }
    public void moveObject(Packet02Move packet){
        System.out.println(networkEntities.size());
        for(NetworkEntity p: networkEntities){
            if(p.isMovable() && packet.getObjectId()==p.getId()){
                if(p instanceof NetworkARPGPlayer){
                    if(!((NetworkARPGPlayer) p).isClientAuthority()){
                        ((MovableNetworkEntity)p).networkMove(packet.getOrientation(),5,packet.getStart());
                    }
                    return;
                }
                ((MovableNetworkEntity)p).networkMove(packet.getOrientation(),5,packet.getStart());

            }
        }
    }

    public boolean spawnObject(Packet00Spawn packet){
        NetworkEntities object = packet.getObject();
        if(object==NetworkEntities.PLAYER){
            for(NetworkARPGPlayer p : players){
                if(p.getId()==packet.getObjectId()){
                    return false;
                }
            }
            var newPlayer = new NetworkARPGPlayer(getCurrentArea(),packet.getOrientation(),packet.getDiscreteCoordinate(),connection, false);
            newPlayer.setId(packet.getObjectId());
            players.add(newPlayer);
            networkEntities.add(newPlayer);
            getCurrentArea().registerActor(newPlayer);
            return true;

        }
        if(object==NetworkEntities.BOMB) new NetworkedBomb(getCurrentArea(),packet.getOrientation(),packet.getDiscreteCoordinate(),connection);
        return  true;
    };
}
