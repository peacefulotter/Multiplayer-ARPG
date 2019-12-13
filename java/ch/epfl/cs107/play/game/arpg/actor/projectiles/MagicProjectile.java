package ch.epfl.cs107.play.game.arpg.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.monster.*;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class MagicProjectile extends Projectile
{
    private final static float MAGIC_DAMAGE = 1;
    private Animation animation;
    private InteractionHandler interactionHandler;
    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param speed
     * @param maxDistance
     */
    public MagicProjectile( Area area, Orientation orientation, DiscreteCoordinates position, int speed, int maxDistance )
    {
        super( area, orientation, position, speed, maxDistance );
        Sprite[] sprites = new Sprite[4];
        for ( int i = 0; i < 4; i++ )
        {
            sprites[ i ] = new Sprite( "zelda/magicWaterProjectile",1,1,this,new RegionOfInterest(i*32,0,32,32) );
        }
        animation = new Animation( 2, sprites, true );
        interactionHandler = new InteractionHandler();
    }

    @Override
    public void update( float deltaTime )
    {
        super.update( deltaTime );
        animation.update( deltaTime );
    }

    @Override
    public void draw( Canvas canvas ) {
        animation.draw( canvas );
    }

    @Override
    public void interactWith( Interactable other ) {
        other.acceptInteraction( interactionHandler );
    }

    @Override
    public void acceptInteraction( AreaInteractionVisitor v ) {
        ((ARPGInteractionVisitor)v).interactWith(this );
    }

    class InteractionHandler implements ARPGInteractionVisitor
    {

        @Override
        public void interactWith( Monster monster )
        {
            monster.giveDamage( MAGIC_DAMAGE );
            if ( monster instanceof DarkLord )
            {
                stopProjectile();
            }
        }

    }
}