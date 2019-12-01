package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Route extends ARPGArea
{
    @Override
    protected void createArea()
    {
        registerActor( new Background( this ) );
        registerActor( new Foreground( this ) );
        registerActor( new Door(
                "zelda/Ferme",
                new DiscreteCoordinates( 18, 15 ),
                Logic.TRUE,
                this,
                Orientation.UP,
                new DiscreteCoordinates(0, 15 ),
                new DiscreteCoordinates( 0, 16 ) ) );
        registerActor( new Door(
                "zelda/Village",
                new DiscreteCoordinates( 29, 18 ),
                Logic.TRUE,
                this,
                Orientation.DOWN,
                new DiscreteCoordinates(9, 0 ),
                new DiscreteCoordinates( 10, 0 ) ) );
        // create grass
        grasses = new Grass[ 10 * 5 ];
        for ( int i = 0; i <= 2; i++ )
        {
            for ( int j = 0; j <= 5; j++ )
            {
                Grass newGrass = new Grass(
                        this,
                        Orientation.DOWN,
                        new DiscreteCoordinates( i+5, j+6 )
                );
                registerActor( newGrass );
                grasses[ 11 * i + j ] = newGrass;
            }
        }
    }

    @Override
    public String getTitle()
    {
        return "zelda/Route";
    }
}
