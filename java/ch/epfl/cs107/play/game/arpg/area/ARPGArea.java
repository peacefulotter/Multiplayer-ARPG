package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.arpg.ARPGBehavior;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Image;
import ch.epfl.cs107.play.window.Window;

public abstract class ARPGArea extends Area
{
    private static int SCALE_FACTOR = 15;
    private Window window;
    private ARPGBehavior behavior;

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


    public ARPGBehavior getBehavior() {
        return behavior;
    }

    @Override
    public final float getCameraScaleFactor() { return SCALE_FACTOR;  }
}
