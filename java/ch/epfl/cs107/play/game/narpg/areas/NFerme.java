package ch.epfl.cs107.play.game.narpg.areas;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.arpg.actor.monster.LogMonster;
import ch.epfl.cs107.play.game.arpg.area.ARPGArea;
import ch.epfl.cs107.play.game.arpg.inventory.items.Coin;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class NFerme extends ARPGArea
{
    @Override
    protected void createArea()
    {
        registerActor(new Background( this ));
        registerActor(new Foreground( this ));
        registerActor( new LogMonster( this, new DiscreteCoordinates( 8, 8 ) ) );
        registerActor(new Coin(this,new DiscreteCoordinates(10,10),50));
    }

    @Override
    public String getTitle()
    {
        return "zelda/Ferme";
    }
}
