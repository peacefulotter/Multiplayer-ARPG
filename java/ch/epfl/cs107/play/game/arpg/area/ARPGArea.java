package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.arpg.ARPGBehavior;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.tutos.Tuto1;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Image;
import ch.epfl.cs107.play.window.Window;

public abstract class ARPGArea extends Area
{
    private Window window;
    private ARPGBehavior behavior;
    protected Grass[] grasses;
    private static int ScaleFactor = 10;

    /**
     * Create the area by adding all its actors
     * called by the begin method, when the area starts to play
     */
    protected abstract void createArea();

    @Override
    public int getWidth() {
        Image behaviorMap = window.getImage( ResourcePath.getBehaviors(getTitle()), null, false);
        return  behaviorMap.getWidth();

    }

    @Override
    public int getHeight() {
        Image behaviorMap = window.getImage(ResourcePath.getBehaviors(getTitle()), null, false);
        return  behaviorMap.getHeight();

    }


    @Override
    public boolean begin( Window window, FileSystem fileSystem )
    {
        this.window = window;
        if ( super.begin(window, fileSystem ) )
        {
            // Set the behavior map
            behavior = new ARPGBehavior( window, getTitle() );
            setBehavior( behavior );
            createArea();
            return true;
        }
        return false;
    }

    @Override
    public final float getCameraScaleFactor() {
        return getWidth() * 3 / 5;
    }

    public Grass getGrass( DiscreteCoordinates coords )
    {
        for ( Grass g : grasses )
        {
            if ( g.getCurrentCells().contains( coords ) )
            {
                return g;
            }
        }
        return null;
    }
}
