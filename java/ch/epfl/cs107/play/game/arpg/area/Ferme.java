package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.monster.FireSpell;
import ch.epfl.cs107.play.game.arpg.actor.monster.FlameSkull;
import ch.epfl.cs107.play.game.arpg.actor.monster.LogMonster;
import ch.epfl.cs107.play.game.arpg.inventory.items.Coin;
import ch.epfl.cs107.play.game.arpg.inventory.items.Heart;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Ferme extends ARPGArea
{
    @Override
    protected void createArea()
    {
        registerActor( new Background( this ) );
        registerActor( new Foreground( this ) );
        registerActor( new Door(
                "zelda/Route",
                new DiscreteCoordinates( 1, 15 ),
                Logic.TRUE,
                this,
                Orientation.RIGHT,
                new DiscreteCoordinates(19, 15 ),
                new DiscreteCoordinates( 19, 16 ) ) );
        registerActor( new Door(
                "zelda/Village",
                new DiscreteCoordinates( 4, 18 ),
                Logic.TRUE,
                this,
                Orientation.DOWN,
                new DiscreteCoordinates(4, 0 ),
                new DiscreteCoordinates( 5, 0 ) ) );
        registerActor( new Door(
                "zelda/Village",
                new DiscreteCoordinates( 14, 18 ),
                Logic.TRUE,
                this,
                Orientation.DOWN,
                new DiscreteCoordinates(13, 0 ),
                new DiscreteCoordinates( 14, 0 ) ) );
        registerActor( new Door(
                "custom/House",
                new DiscreteCoordinates( 14, 2 ),
                Logic.TRUE,
                this,
                Orientation.UP,
                new DiscreteCoordinates( 6, 11 ) ) );
        registerActor( new Coin(this, new DiscreteCoordinates(10,10), 50));
        registerActor( new Heart(this,  new DiscreteCoordinates(9,10)));
        registerActor( new FlameSkull( this, new DiscreteCoordinates( 2, 15 ) ));
        registerActor( new LogMonster( this, new DiscreteCoordinates( 8, 8 ) ));
        registerActor( new FireSpell( this, Orientation.DOWN, new DiscreteCoordinates( 10, 9 ), 0.6f, 4 ) );
        registerActor( new FireSpell( this, Orientation.DOWN, new DiscreteCoordinates( 9, 9 ), 0.6f, 7 ) );
    }

    @Override
    public String getTitle()
    {
        return "zelda/Ferme";
    }
}
