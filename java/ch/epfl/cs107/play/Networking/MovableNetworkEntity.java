package ch.epfl.cs107.play.Networking;

import ch.epfl.cs107.play.Networking.Packets.Packet02Move;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public interface MovableNetworkEntity extends NetworkEntity {
    void networkMove(Packet02Move movePacket);
    @Override
    default boolean isMovable() {
        return true;
    }
}