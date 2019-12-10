package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.ConnectionHandler;
import ch.epfl.cs107.play.Networking.MovableNetworkIdentity;
import ch.epfl.cs107.play.Networking.Packets.Packet02Move;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Random;

public class NetworkARPGPlayer extends ARPGPlayer implements MovableNetworkIdentity {
    private Connection connection;
    private int id;
    /**
     * Default Player constructor
     *
     * @param area        (Area): Owner Area, not null
     * @param orientation (Orientation): Initial player orientation, not null
     * @param coordinates (Coordinates): Initial position, not null
     */
    public NetworkARPGPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates, Connection connection) {
        super(area, orientation, coordinates);
        this.connection = connection;
        this.id= new Random().nextInt();
    }

    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        boolean moved=false;
        System.out.println(connection);
        if(connection !=null){
            if(keyboard.get(keyboard.UP).isDown()) moved=true;
            else if(keyboard.get(keyboard.DOWN).isDown()) moved=true;
            else if(keyboard.get(keyboard.LEFT).isDown()) moved=true;
            else if(keyboard.get(keyboard.RIGHT).isDown()) moved=true;
        }

        if(moved){
            Packet02Move packet = new Packet02Move(id,getOrientation(),getCurrentMainCellCoordinates());
            packet.writeData(connection);
        }

        super.update(deltaTime);
    }

    public void updatePlayerState(Orientation orientation){
        System.out.println(orientation);;
        setAnimationByOrientation(orientation);
        if(orientation==getOrientation()){
            move(8);
        }else{
            orientate(orientation);
        }
    }


    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void setPosition(DiscreteCoordinates position) {
        setPosition(position);
    }

    @Override
    public void setOrientation(Orientation orientation) {
        setOrientation(orientation);
    }

    @Override
    public void move(Orientation orientation, int Speed, DiscreteCoordinates startPosition) {
        setOrientation(orientation);
        setPosition(startPosition);
        move(Speed);
    }
}
