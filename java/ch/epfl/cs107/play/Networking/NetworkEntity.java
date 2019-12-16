package ch.epfl.cs107.play.Networking;

import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.actor.Actor;

import java.util.HashMap;

public interface NetworkEntity extends Actor
{
    int getId();
    default boolean isMovable() { return false; }
    Packet00Spawn getSpawnPacket();
    default void updateState(HashMap<String,String> updateMap){};
}
