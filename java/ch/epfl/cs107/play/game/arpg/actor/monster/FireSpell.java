package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
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

public class FireSpell extends AreaEntity implements Interactor
{
    private static final float MIN_LIFE_TIME = 3;
    private static final float MAX_LIFE_TIME = 5;
    private static final float PROPAGATION_TIME_SPELL = 0.5f;

    private final float damage;
    private final float lifeTime;
    private final List<DiscreteCoordinates> currentCell;
    private final FireSpellHandler handler;

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
        handler = new FireSpellHandler();

        Sprite[] animationSprites = new Sprite[7];
        for (int i = 0; i < 7; i++) {
            animationSprites[i] = new Sprite("zelda/fire", 1, 1, this, new RegionOfInterest(i * 16, 16, 16, 16), Vector.ZERO, 1f, -100);
        }
        fireSpellAnimation = new Animation( 7, animationSprites, false);
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
        fireSpellAnimation.update( deltaTime );
        fireTimeAlive += deltaTime;
        super.update( deltaTime );
    }

    @Override
    public void draw( Canvas canvas )
    {
        fireSpellAnimation.draw( canvas );
    }

    private void generateFireSpell()
    {
        DiscreteCoordinates newPosition = getFieldOfViewCells().get( 0 );
        boolean spawned = getOwnerArea().unregisterActor(
                new FireSpell( getOwnerArea(), getOrientation(), newPosition, damage-0.1f ) );
        System.out.println(spawned);
    }


    @Override
    public List<DiscreteCoordinates> getCurrentCells()
    {
        return currentCell;
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells()
    {
        DiscreteCoordinates cell = currentCell.get( 0 );
        List<DiscreteCoordinates> viewCells = new ArrayList<>();
        switch( getOrientation() )
        {
            case UP:
                viewCells.add( new DiscreteCoordinates( cell.x, cell.y + 1 ) );
                break;
            case RIGHT:
                viewCells.add( new DiscreteCoordinates( cell.x + 1, cell.y ) );
                break;
            case DOWN:
                viewCells.add( new DiscreteCoordinates( cell.x, cell.y - 1 ) );
                break;
            case LEFT:
                viewCells.add( new DiscreteCoordinates( cell.x - 1, cell.y ) );
                break;
        }
        return viewCells;
    }

    @Override
    public boolean wantsCellInteraction()
    {
        return false;
    }

    @Override
    public boolean wantsViewInteraction()
    {
        return false;
    }

    @Override
    public void interactWith(Interactable other)
    {

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
        System.out.println("flame interact");
        ((ARPGInteractionVisitor)v).interactWith( this );
    }


    class FireSpellHandler implements ARPGInteractionVisitor
    {
        @Override
        public void interactWith( ARPGPlayer player )
        {
            player.giveDamage( damage );
        }

        @Override
        public void interactWith( Monster monster )
        {
            if ( monster.getVulnerabilities().contains( Vulnerabilities.FIRE ) )
            {
                monster.giveDamage( damage, Vulnerabilities.FIRE );
            }
        }
    }

}
