package ch.epfl.cs107.play.Networking;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public interface MovableNetworkEntity extends NetworkEntity {
    void networkMove(Orientation orientation, int Speed, DiscreteCoordinates startPosition);
    @Override
    default boolean isMovable() {
        return true;
    }
}