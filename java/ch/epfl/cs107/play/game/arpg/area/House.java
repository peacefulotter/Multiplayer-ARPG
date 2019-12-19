package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class House extends ARPGArea
{
    @Override
    protected void createArea()
    {
        registerActor( new Background( this ) );
        registerActor( new Door(
                "zelda/Ferme",
                new DiscreteCoordinates( 6, 10 ),
                Logic.TRUE,
                this,
                Orientation.DOWN,
                new DiscreteCoordinates( 14, 1 ) ) );
    }

    @Override
    public String getTitle()
    {
        return "custom/House";
    }
}
