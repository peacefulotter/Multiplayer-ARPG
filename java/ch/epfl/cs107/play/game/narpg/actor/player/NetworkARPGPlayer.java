package ch.epfl.cs107.play.game.narpg.actor.player;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.MovableNetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet02Move;
import ch.epfl.cs107.play.Networking.utils.IdGenerator;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Random;

public class NetworkARPGPlayer extends ARPGPlayer implements MovableNetworkEntity {
    private Connection connection;
    private int id;
    private boolean clientAuthority;
    /**
     * Default Player constructor
     *
     * @param area        (Area): Owner Area, not null
     * @param orientation (Orientation): Initial player orientation, not null
     * @param coordinates (Coordinates): Initial position, not null
     */
    public NetworkARPGPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates, Connection connection, boolean clientAuthority) {
        super(area, orientation, coordinates);
        this.connection = connection;
        this.id= IdGenerator.generateId();
        this.clientAuthority=clientAuthority;
    }

    @Override
    public void update(float deltaTime) {
        if(!connection.isServer()){
            Keyboard keyboard = getOwnerArea().getKeyboard();
            boolean moved=false;
            if(connection !=null){
                if(keyboard.get(keyboard.UP).isDown()) moved=true;
                else if(keyboard.get(keyboard.DOWN).isDown()) moved=true;
                else if(keyboard.get(keyboard.LEFT).isDown()) moved=true;
                else if(keyboard.get(keyboard.RIGHT).isDown()) moved=true;
            }

            if(moved){
                System.out.println("sending move");
                Packet02Move packet = new Packet02Move(id,getOrientation(),getCurrentMainCellCoordinates());
                packet.writeData(connection);
            }
        }
        super.update(deltaTime);
    }

    public void updatePlayerState(Orientation orientation){
        setAnimationByOrientation(orientation);
        if(orientation==getOrientation()){
            move(8);
        }else{
            orientate(orientation);
        }
    }


    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setPosition(DiscreteCoordinates position) {
    }

    @Override
    public void setOrientation(Orientation orientation) {
        orientate(orientation);
    }

    @Override
    public void networkMove(Orientation orientation, int Speed, DiscreteCoordinates startPosition) {
        moveOrientate(orientation);
    }

    public void setId(int objectId) {
        this.id=objectId;
    }

    public boolean isClientAuthority() {
        return clientAuthority;
    }
}
