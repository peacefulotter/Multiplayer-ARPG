package ch.epfl.cs107.play.game.narpg.actor;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.sql.SQLOutput;

public class NetworkedBomb extends Bomb implements NetworkEntity {
    public NetworkedBomb(Area area, Orientation orientation, DiscreteCoordinates position, Connection connection) {
        super(area, orientation, position);
    }

    @Override
    public int getId() {
        return NetworkEntities.BOMB.getClassId();
    }

    @Override
    public void setPosition(DiscreteCoordinates position) {

    }

    @Override
    public void setOrientation(Orientation orientation) {

    }

    @Override
    public Packet00Spawn getSpawnPacket() {
        System.out.println("spanw packet bomb");
        return null;
    }
}
