package ch.epfl.cs107.play.game.arpg.inventory.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Coin extends CollectibleAreaEntity
{
    private final static int ANIMATION_DURATION = 6;
    private final Animation animation;
    private final int value;
    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Coin( Area area, DiscreteCoordinates position, int value )
    {
        super( area, position );
        this.value = value;
        RPGSprite[] sprites = new RPGSprite[ 4 ];
        for (int i = 0; i < 4; i++) {
            sprites[ i ] = new RPGSprite( "zelda/coin", 1, 1, this, new RegionOfInterest(i * 16, 0, 16, 16) );
        }
        animation = new Animation( ANIMATION_DURATION, sprites, true );
    }

    @Override
    public void update( float deltaTime ) { animation.update( deltaTime ); }

    @Override
    public void draw( Canvas canvas ) {
        animation.draw(canvas);
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public void acceptInteraction( AreaInteractionVisitor v )
    {
        ((ARPGInteractionVisitor)v).interactWith(this);
    }
}
