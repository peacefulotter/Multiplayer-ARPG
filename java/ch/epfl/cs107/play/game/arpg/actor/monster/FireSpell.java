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
    private static final float MIN_LIFE_TIME = 3f;
    private static final float MAX_LIFE_TIME = 5f;
    private static final float PROPAGATION_TIME_SPELL = 0.5f;
    private static final float ATTACK_COUNTDOWN = 1f;

    private final float damage;
    private final float lifeTime;
    private final int force;
    private final Orientation orientation;
    private final List<DiscreteCoordinates> currentCell;
    private final FireSpellHandler handler;

    private Animation fireSpellAnimation;
    private float fireTimeAlive;
    private float timeAttack;
    private boolean hasPropagated;
    public boolean hasAttacked;

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
        if ( hasAttacked )
        {
            if ( timeAttack > ATTACK_COUNTDOWN )
            {
                hasAttacked = false;
                timeAttack = 0;
            }
            timeAttack += deltaTime;
        }
        if ( force > 0 && !hasPropagated && fireTimeAlive >= PROPAGATION_TIME_SPELL )
        {
            generateFireSpell();
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
        hasPropagated = true;

        if ( damage-0.1f <= 0 ) { return; }

        DiscreteCoordinates newPosition = getFieldOfViewCells().get( 0 );
        FireSpell fireSpell = new FireSpell( getOwnerArea(), orientation, newPosition, damage, force-1 );
        boolean canSpawn = getOwnerArea().canEnterAreaCells( fireSpell, Collections.singletonList( newPosition ) );
        if ( canSpawn )
        {
            getOwnerArea().registerActor( fireSpell );
        }
    }

    public void blow()
    {
        getOwnerArea().unregisterActor( this );
    }

    public float getDamage() { return damage; }


    @Override
    public List<DiscreteCoordinates> getCurrentCells()
    {
        return currentCell;
    }

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
