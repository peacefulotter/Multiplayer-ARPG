package ch.epfl.cs107.play.game.arpg.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.monster.*;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Arrow extends Projectile
{
    private final static float ARROW_DAMAGE = 0.5f;
    private Sprite sprite;
    // type ARPGInteractionVisitor, not private and not final because it will be overwritten by NetworkArrow
    protected ARPGInteractionVisitor handler;

    private enum Directions
    {
        UP( 0 ),
        RIGHT( 1 ),
        DOWN( 2 ),
        LEFT( 3 );
        private int directionIndex;

        Directions( int directionIndex )
        {
            this.directionIndex = directionIndex;
        }

        public int value() {
            return this.directionIndex;
        }

    }

    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param speed
     * @param maxDistance
     */

    public Arrow( Area area, Orientation orientation, DiscreteCoordinates position, int speed, int maxDistance )
    {
        super( area, orientation, position, speed, maxDistance );
        handler = new ArrowInteractionHandler();
        int spriteOffset = 0;
        switch ( orientation )
        {
            case UP:
                spriteOffset = Directions.UP.value();
                break;
            case DOWN:
                spriteOffset = Directions.DOWN.value();
                break;
            case RIGHT:
                spriteOffset = Directions.RIGHT.value();
                break;
            case LEFT:
                spriteOffset = Directions.LEFT.value();
                break;
        }
        sprite = new Sprite( "zelda/arrow", 1, 1, this, new RegionOfInterest(spriteOffset * 32, 0, 32, 32) );
    }

    @Override
    public void draw( Canvas canvas ) {
        sprite.draw( canvas );
    }


    @Override
    public void interactWith( Interactable other ) {
        other.acceptInteraction( handler );
    }

    @Override
    public void acceptInteraction( AreaInteractionVisitor v ) {
        ((ARPGInteractionVisitor)v).interactWith( this );
    }


    class ArrowInteractionHandler implements ARPGInteractionVisitor
    {

        @Override
        public void interactWith( Monster monster )
        {
            if ( monster.getVulnerabilities().contains( Vulnerabilities.LONG_RANGE ) )
            {
                monster.giveDamage( ARROW_DAMAGE );
            }
            if ( !(monster instanceof FlameSkull) )
            {
                stopProjectile();
            }
        }

        @Override
        public void interactWith( Grass grass )
        {
            stopProjectile();
            grass.cutGrass();
        }

        @Override
        public void interactWith( Bomb bomb )
        {
            stopProjectile();
            bomb.explode();
        }

    }

}
