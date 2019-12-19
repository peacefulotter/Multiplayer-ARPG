package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.arpg.actor.projectiles.MagicProjectile;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FireSpell extends AreaEntity implements Interactor
{
    // the firespell can live between Min life time and Max life time
    private static final float MIN_LIFE_TIME = 3f;
    private static final float MAX_LIFE_TIME = 5f;
    // the time it takes to propagate
    private static final float PROPAGATION_TIME_SPELL = 0.5f;
    // the time it takes to deal damage again
    private static final float ATTACK_COOLDOWN = 1f;

    private final float damage;
    private final float lifeTime;
    private final int force;
    private final Orientation orientation;
    private final List<DiscreteCoordinates> currentCell;
    private final FireSpellHandler handler;

    private Animation fireSpellAnimation;
    // time since the fire spell is alive
    private float fireTimeAlive;
    // time since the fire spell dealt damage
    private float timeAttack;
    // check whether the fire spell already propagated
    private boolean hasPropagated;
    // check if the fire spell just attacked
    private boolean hasAttacked;

    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public FireSpell(Area area, Orientation orientation, DiscreteCoordinates position, float damage, int force )
    {
        super( area, orientation, position );
        this.orientation = orientation;
        this.damage = damage;
        this.force = force;
        // initialise the fire spell lifetime with a random number
        lifeTime = MIN_LIFE_TIME + RandomGenerator.getInstance().nextFloat() * (MAX_LIFE_TIME-MIN_LIFE_TIME);
        currentCell = new ArrayList<>();
        currentCell.add( position );
        fireTimeAlive = 0;
        hasPropagated = false;
        timeAttack = 0;
        handler = new FireSpellHandler();

        Sprite[] animationSprites = new Sprite[7];
        for (int i = 0; i < 7; i++) {
            animationSprites[i] = new Sprite("zelda/fire", 1, 1, this, new RegionOfInterest(i * 16, 0, 16, 16), new Vector( 0, 0.25f ), 1f, -100);
        }
        fireSpellAnimation = new Animation( 7, animationSprites, true);
    }

    @Override
    public void update(float deltaTime)
    {
        // if the fire spell just attacked
        if ( hasAttacked )
        {
            // then we check if the cooldown is over
            if ( timeAttack > ATTACK_COOLDOWN )
            {
                // reset the attack and the time since last attack
                hasAttacked = false;
                timeAttack = 0;
            }
            // increase the tile since last attack
            timeAttack += deltaTime;
        }
        // if the fire spell can propagate (force>0) and the time it is alive is enough (equals or more to PROPAGATION_TIME_SPELL)
        if ( force > 0 && !hasPropagated && fireTimeAlive >= PROPAGATION_TIME_SPELL )
        {
            // then it genereates a new fire spell
            generateFireSpell();
        }
        // or if the fire spell has reached his death time
        else if ( fireTimeAlive >= lifeTime )
        {
            // then it gets unregistered
            blow();
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

    /**
     * Create a new fire spell in front on it
     */
    private void generateFireSpell()
    {
        // the fire spell has propagated even though it cannot create a new one in front of him
        hasPropagated = true;
        // get the coordinates of the cell in front of him
        DiscreteCoordinates newPosition = getFieldOfViewCells().get( 0 );
        // create an instance of FireSpell with a force that is -1
        FireSpell fireSpell = new FireSpell( getOwnerArea(), orientation, newPosition, damage, force-1 );
        // check if it is possible to spawn it
        boolean canSpawn = getOwnerArea().canEnterAreaCells( fireSpell, Collections.singletonList( newPosition ) );
        if ( canSpawn )
        {
            // register the new fire spell in the area
            getOwnerArea().registerActor( fireSpell );
        }
    }


    /**
     * Unregister the fire spell from the area
     */
    public void blow()
    {
        getOwnerArea().unregisterActor( this );
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells()
    {
        return currentCell;
    }

    /**
     * The fire spell field of view is only the cell located in front of him
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells()
    {
        Vector cell = currentCell.get( 0 ).toVector().add( getOrientation().toVector() );
        List<DiscreteCoordinates> viewCells = new ArrayList<>();
        viewCells.add( new DiscreteCoordinates( (int)cell.x, (int)cell.y ) );
        return viewCells;
    }

    @Override
    public boolean wantsCellInteraction()
    {
        return true;
    }

    @Override
    public boolean wantsViewInteraction()
    {
        return false;
    }

    @Override
    public void interactWith( Interactable other )
    {
        other.acceptInteraction( handler );
    }

    @Override
    public boolean takeCellSpace()
    {
        return false;
    }

    @Override
    public boolean isCellInteractable()
    {
        return false;
    }

    @Override
    public boolean isViewInteractable()
    {
        return false;
    }

    @Override
    public void acceptInteraction( AreaInteractionVisitor v )
    {
        ((ARPGInteractionVisitor)v).interactWith( this );
    }


    class FireSpellHandler implements ARPGInteractionVisitor
    {

        @Override
        public void interactWith( ARPGPlayer player )
        {
            if ( !hasAttacked )
            {
                player.giveDamage( damage );
                hasAttacked = true;
            }
        }

        @Override
        public void interactWith( Monster monster )
        {
            if ( !hasAttacked && monster.getVulnerabilities().contains( Vulnerabilities.FIRE ) )
            {
                hasAttacked = true;
                monster.giveDamage( damage );
            }
        }

        @Override
        public void interactWith( Grass grass )
        {
            grass.cutGrass();
        }


        /*
            Arrows and MagicProjectile can blow the fire spell
         */
        @Override
        public void interactWith( Arrow arrow )
        {
            blow();
        }

        @Override
        public void interactWith( MagicProjectile magicProjectile )
        {
            blow();
        }
    }
}
