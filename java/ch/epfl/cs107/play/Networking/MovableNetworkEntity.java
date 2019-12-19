package ch.epfl.cs107.play.Networking;

import ch.epfl.cs107.play.Networking.Packets.Packet02Move;

public interface MovableNetworkEntity extends NetworkEntity {
    void networkMove(Packet02Move movePacket);
    @Override
    default boolean isMovable() {
        return true;
    }
}