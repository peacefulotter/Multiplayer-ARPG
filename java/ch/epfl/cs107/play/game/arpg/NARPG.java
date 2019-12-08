package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.Networking.ConnectionHandler;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

import java.util.List;

public class NARPG extends ARPG {
    private ConnectionHandler connectionHandler;
    private boolean isServer;

    public NARPG(boolean isServer) {
        super();
        this.isServer = isServer;
    }


    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            Area area = setCurrentArea("zelda/Ferme", true);
            player = new NetworkARPGPlayer(getCurrentArea(), Orientation.DOWN, new DiscreteCoordinates(6, 10), connectionHandler);
            initPlayer(player);
            return true;
        }
        return false;

    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public void updatePlayerState(int i) {
        System.out.println(i);
        switch (i) {
            case Keyboard.UP:
                ((NetworkARPGPlayer) player).updatePlayerState(Orientation.UP);
                break;
            case Keyboard.RIGHT:
                ((NetworkARPGPlayer) player).updatePlayerState(Orientation.RIGHT);
                break;
            case Keyboard.DOWN:
                ((NetworkARPGPlayer) player).updatePlayerState(Orientation.DOWN);
                break;
            case Keyboard.LEFT:
                ((NetworkARPGPlayer) player).updatePlayerState(Orientation.LEFT);
                break;

        }
    }
}
