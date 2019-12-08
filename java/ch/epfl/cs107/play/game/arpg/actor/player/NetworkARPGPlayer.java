package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.Networking.ConnectionHandler;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;

public class NetworkARPGPlayer extends ARPGPlayer{
    private ConnectionHandler connectionHandler;
    /**
     * Default Player constructor
     *
     * @param area        (Area): Owner Area, not null
     * @param orientation (Orientation): Initial player orientation, not null
     * @param coordinates (Coordinates): Initial position, not null
     */
    public NetworkARPGPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates, ConnectionHandler connectionHandler) {
        super(area, orientation, coordinates);
        this.connectionHandler=connectionHandler;
    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        if(connectionHandler!=null){
            if(keyboard.get(keyboard.UP).isDown()) connectionHandler.sendData(Integer.toString(Keyboard.UP));
            else if(keyboard.get(keyboard.DOWN).isDown()) connectionHandler.sendData(Integer.toString(Keyboard.DOWN));
            else if(keyboard.get(keyboard.LEFT).isDown()) connectionHandler.sendData(Integer.toString(Keyboard.LEFT));
            else if(keyboard.get(keyboard.RIGHT).isDown()) connectionHandler.sendData(Integer.toString(Keyboard.RIGHT));
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

}
