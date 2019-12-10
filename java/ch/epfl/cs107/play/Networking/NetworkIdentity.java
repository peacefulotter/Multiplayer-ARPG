package ch.epfl.cs107.play.Networking;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public interface NetworkIdentity {
    int getId();
    void setPosition(DiscreteCoordinates position);
    void setOrientation(Orientation orientation);
}
