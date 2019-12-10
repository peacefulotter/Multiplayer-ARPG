package ch.epfl.cs107.play.Networking;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public interface MovableNetworkIdentity extends  NetworkIdentity{
    void move(Orientation orientation, int Speed, DiscreteCoordinates startPosition);
}