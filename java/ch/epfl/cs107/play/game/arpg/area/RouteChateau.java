package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.CastleDoor;
import ch.epfl.cs107.play.game.arpg.inventory.items.CastleKey;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class RouteChateau extends ARPGArea {
    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Door("zelda/Route",new DiscreteCoordinates(9,18), Logic.TRUE, this ,Orientation.DOWN,new DiscreteCoordinates(9,0),new DiscreteCoordinates(10,0)));
        registerActor(new CastleDoor(this));
        registerActor(new CastleKey(this, new DiscreteCoordinates(9,10)));
    }


    @Override
    public String getTitle() {
        return "zelda/RouteChateau";
    }
}
