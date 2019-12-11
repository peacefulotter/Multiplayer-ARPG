package ch.epfl.cs107.play.game.narpg.actor;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class NetworkedBomb extends Bomb implements NetworkEntity {
    public NetworkedBomb(Area area, Orientation orientation, DiscreteCoordinates position, Connection connection) {
        super(area, orientation, position);
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void setPosition(DiscreteCoordinates position) {

    }

    @Override
    public void setOrientation(Orientation orientation) {

    }

    @Override
    public Packet00Spawn getSpawnPacket() {
        return null;
    }
}
