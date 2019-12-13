package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.area.*;
import ch.epfl.cs107.play.game.rpg.RPG;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class ARPG extends RPG {

    private ARPGPlayer player;

    public ARPG() {
        super();
    }


    @Override
    public String getTitle() {
        return "ZeldIC";
    }

    /**
     * Add all the areas
     */
    protected void createAreas()
    {
        addArea( new Ferme() );
        addArea( new Village() );
        addArea( new Route() );
        addArea( new RouteChateau() );
        addArea( new Chateau() );
        addArea( new House() );
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            Area area = setCurrentArea("zelda/Ferme", true);
            player = new ARPGPlayer(area, Orientation.DOWN, new DiscreteCoordinates(6, 9));
            initPlayer(player);
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
            Area newArea = setCurrentArea(currentDoor.getDestination(), false);
            player.enterArea(newArea, currentDoor.getOtherSideCoordinates());

        }
        super.update(deltaTime);
    }



    @Override
    public void end() {
    }
}
