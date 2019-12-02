package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.area.Ferme;
import ch.epfl.cs107.play.game.arpg.area.Route;
import ch.epfl.cs107.play.game.arpg.area.Village;
import ch.epfl.cs107.play.game.rpg.RPG;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class ARPG extends RPG
{
    @Override
    public String getTitle()
    {
        return "ZeldIC";
    }

    //public final static float CAMERA_SCALE_FACTOR = 13.f;
    //public final static float STEP = 0.05f;

    private ARPGPlayer player;

    private final String[] areas = {
            "zelda/Ferme",
            "zelda/Village",
            "zelda/Route"
    };
    /*private final DiscreteCoordinates[] startingPositions = {
            new DiscreteCoordinates(2,10),
            new DiscreteCoordinates(5,15)
    };*/


    /**
     * Add all the areas
     */
    private void createAreas(){
        addArea( new Ferme() );
        addArea( new Village() );
        addArea( new Route() );
    }

    @Override
    public boolean begin( Window window, FileSystem fileSystem )
    {
        if ( super.begin( window, fileSystem ) )
        {
            createAreas();
            Area area = setCurrentArea( "zelda/Ferme", true );
            player = new ARPGPlayer( area, Orientation.DOWN, new DiscreteCoordinates(6,10) );
            initPlayer( player );
            return true;
        }
        return false;
    }

    @Override
    public void update( float deltaTime )
    {
        if ( player.isPassingADoor() )
        {
            player.leaveArea();
            Door currentDoor = player.passedDoor();
            Area newArea = setCurrentArea( currentDoor.getDestination(), true );
            player.enterArea( newArea, currentDoor.getOtherSideCoordinates() );
        }
        super.update(deltaTime);
    }

    @Override
    public void end() { }
}
