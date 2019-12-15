package ch.epfl.cs107.play.Networking;

import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.HashMap;

public interface NetworkEntity extends Actor {
    int getId();
    void setPosition( DiscreteCoordinates position );
    void setOrientation( Orientation orientation );
    default boolean isMovable() { return false; }
    Packet00Spawn getSpawnPacket();
    default void updateState(HashMap<String,String> updateMap){};
}
