package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.Packets.Packet;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;


public class NARPG extends ARPG {
    private Connection connection;
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
            player = new NetworkARPGPlayer(getCurrentArea(), Orientation.DOWN, new DiscreteCoordinates(6, 10), connection);
            initPlayer(player);
            return true;
        }
        return false;

    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void updateState(Packet packet) {
        System.out.println(packet.getObjectId());
    }
}
