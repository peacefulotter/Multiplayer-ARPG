package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.List;

public class FireSpell extends AreaEntity
{
    private static final float MIN_LIFE_TIME = 120;
    private static final float MAX_LIFE_TIME = 240;
    private static final float PROPAGATION_TIME_SPELL = 20;

    private final float damage;
    private final float lifeTime;
    private final List<DiscreteCoordinates> currentCell;

    private Animation fireSpellAnimation;
    private float fireTimeAlive;
    private boolean hasPropagated;

    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public FireSpell(Area area, Orientation orientation, DiscreteCoordinates position, float damage )
    {
        super( area, orientation, position );
        this.damage = damage;
        lifeTime = MIN_LIFE_TIME + RandomGenerator.getInstance().nextFloat() * (MAX_LIFE_TIME-MIN_LIFE_TIME);
        currentCell = new ArrayList<>();
        currentCell.add( position );
        fireTimeAlive = 0;
        hasPropagated = false;

        Sprite[] animationSprites = new Sprite[7];
        for (int i = 0; i < 7; i++) {
            animationSprites[i] = new Sprite("zelda/fire", 1.5f, 1.5f, this, new RegionOfInterest(i * 32, 0, 32, 32), Vector.ZERO, 1f, -100);
        }
        fireSpellAnimation = new Animation( 12, animationSprites, false);
    }

    @Override
    public void update(float deltaTime)
    {
        if ( !hasPropagated && fireTimeAlive >= PROPAGATION_TIME_SPELL )
        {
            generateFireSpell();
            hasPropagated = true;
        }
        else if ( fireTimeAlive >= lifeTime )
        {
            getOwnerArea().unregisterActor( this );
        }
        else
        {
            fireSpellAnimation.update( deltaTime );
        }
        super.update( deltaTime );
    }

    @Override
    public void draw(Canvas canvas)
    {
        fireSpellAnimation.draw( canvas );
    }

    private void generateFireSpell()
    {

    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells()
    {
        return currentCell;
    }

    @Override
    public boolean takeCellSpace()
    {
        return false;
    }

    @Override
    public boolean isCellInteractable()
    {
        return true;
    }

    @Override
    public boolean isViewInteractable()
    {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v)
    {
        ((ARPGInteractionVisitor)v).interactWith( this );
    }

}
