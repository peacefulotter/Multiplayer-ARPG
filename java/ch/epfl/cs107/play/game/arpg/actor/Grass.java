package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.game.rpg.handler.RPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Grass extends AreaEntity
{
    private boolean isCut = false;
    private final List<DiscreteCoordinates> currentCells;
    private final Sprite sprite = new Sprite(
            "zelda/grass",
            1, 1,
            this, new RegionOfInterest( 0, 0, 16, 16 )
    );

    private Sprite[][] grassSprites = new Sprite[ 4 ][ 1 ];
    private Animation[] grassAnimation;
    private int currentAnimationIndex = 0;

    public Grass( Area area, Orientation orientation, DiscreteCoordinates position )
    {
        super( area, orientation, position );
        currentCells = new ArrayList<>();
        currentCells.add( position );

        for ( int i = 0; i < 4; i++ )
        {
            grassSprites[ i ][ 0 ] = new Sprite( "zelda/grass.sliced", 1f, 1f, this, new RegionOfInterest( i*16, 0, 16, 16 ), Vector.ZERO,  1f, 1f );
        }

        // crée un tableau de 4 animation
        grassAnimation = RPGSprite.createAnimations(6, grassSprites );
        System.out.println( Arrays.toString( grassAnimation ) );
    }

    @Override
    public void draw( Canvas canvas )
    {
        System.out.println(isCut);
        if ( !isCut )
        {
            sprite.draw( canvas );
        }
        else
        {
            grassAnimation[ currentAnimationIndex ].draw( canvas );
        }
        if ( grassAnimation[grassAnimation.length-1].isCompleted())
        {
            getOwnerArea().unregisterActor( this );
        }
    }

    protected void cutGrass()
    {
        System.out.println("cut");
        isCut = true;
    }


    @Override
    public List<DiscreteCoordinates> getCurrentCells()
    {
        return currentCells;
    }

    @Override
    public boolean takeCellSpace()
    {
        return isCut;
    }

    @Override
    public boolean isCellInteractable()
    {
        return true;
    }

    @Override
    public boolean isViewInteractable()
    {
        return true;
    }

    @Override
    public void acceptInteraction( AreaInteractionVisitor v )
    {
        ((RPGInteractionVisitor)v).interactWith(this);
    }
}
