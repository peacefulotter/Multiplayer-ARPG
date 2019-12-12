package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Monster extends MovableAreaEntity implements Interactor
{
    private final int ANIMATION_DURATION = 10;
    private final double CRITS_PERCENTAGE = 0.2;

    private final Orientation[] orientations;
    private final Sprite critsSprite;
    private final String name;
    private final float maxHealth;

    private List<DiscreteCoordinates> currentCells;
    private List<Vulnerabilities> vulnerabilities;
    private Animation[] movementAnimation;
    private float currentHealth;
    private boolean dealtCrits;

    protected int currentAnimationIndex = 2;
    protected final float PLAYER_DAMAGE;
    protected Animation deathAnimation;
    protected boolean isDead;

    public Monster(
            Area area, DiscreteCoordinates position, Orientation[] orientations,
            String name, String spriteName, float maxHealth, float damage, int nbFrames,
            Vector spriteOffset, Vulnerabilities... vulnerabilities)
    {
        super( area, Orientation.DOWN, position );
        this.name = name;
        this.maxHealth = maxHealth;
        currentHealth = maxHealth;
        currentCells = new ArrayList<>();
        currentCells.add( position );
        isDead = false;
        PLAYER_DAMAGE = damage;
        this.orientations = orientations;

        this.vulnerabilities = new ArrayList<>();
        Collections.addAll( this.vulnerabilities, vulnerabilities );

        Sprite[] deathAnimationSprites = new Sprite[7];
        for ( int i = 0; i < 7; i++ ) {
            deathAnimationSprites[i] = new Sprite("zelda/vanish", 1f, 1f, this, new RegionOfInterest(i * 32, 0, 32, 32), Vector.ZERO, 1f, 1);
        }
        deathAnimation = new Animation(7, deathAnimationSprites, false);

        Sprite[][] sprites = RPGSprite.extractSprites( spriteName,
                nbFrames, 2, 2,
                this, 32, 32, spriteOffset, orientations);
        movementAnimation = RPGSprite.createAnimations(ANIMATION_DURATION, sprites);

        critsSprite = new Sprite( "custom/crits", 1.5f, 1.5f, this, new RegionOfInterest( 0, 0, 541, 541 ), new Vector( -0.25f, 0.75f ), 1, 1000 );
        dealtCrits = false;
    }


    public List<Vulnerabilities> getVulnerabilities()
    {
        return vulnerabilities;
    }

    @Override
    public void update( float deltaTime )
    {
        update( deltaTime, true );
    }

    public void update(float deltaTime, boolean allowReorientation )
    {
        if ( !isDead )
        {
            if ( allowReorientation )
            {
                Orientation newOrientation = getRandomOrientation();
                if ( Math.random() < 0.01 )
                {
                    boolean orientationSuccessful = orientate( newOrientation );
                    if ( orientationSuccessful )
                    {
                        changeAnimationIndex( newOrientation );
                    }
                    move( ANIMATION_DURATION );
                }
            }
            movementAnimation[currentAnimationIndex].update(deltaTime);
        }
        else if ( !deathAnimation.isCompleted() )
        {
            deathAnimation.update( deltaTime );
        } else {
            getOwnerArea().unregisterActor( this );
        }

        if ( dealtCrits )
        {
            critsSprite.setAlpha( critsSprite.getAlpha() - 0.01f );
            if ( critsSprite.getAlpha() <= 0 )
            {
                critsSprite.setAlpha( 1 );
                dealtCrits = false;
            }
        }

        super.update( deltaTime );
    }


    private void changeAnimationIndex( Orientation newOrientation )
    {
        switch ( newOrientation )
        {
            case UP:
                currentAnimationIndex = 0;
                break;
            case DOWN:
                currentAnimationIndex = 2;
                break;
            case LEFT:
                currentAnimationIndex = 3;
                break;
            case RIGHT:
                currentAnimationIndex = 1;
                break;
        }

    }

    @Override
    public void draw( Canvas canvas )
    {
        draw( canvas, true );
    }

    public void draw( Canvas canvas, boolean drawPlayer )
    {
        if ( !isDead )
        {
            if ( drawPlayer )
            {
                movementAnimation[ currentAnimationIndex ].draw( canvas );
            }
            if ( dealtCrits )
            {
                critsSprite.draw( canvas );
            }
        }
        else
        {
            deathAnimation.draw( canvas );
        }
    }

    protected Orientation getRandomOrientation()
    {
        int random = RandomGenerator.getInstance().nextInt( 4 );
        return Orientation.fromInt( random );
    }

    abstract protected void onMove();

    @Override
    public List<DiscreteCoordinates> getCurrentCells()
    {
        return Collections.singletonList( getCurrentMainCellCoordinates() );
    }

    public void giveDamage( float damage )
    {
        float crits = 1;
        if ( Math.random() < CRITS_PERCENTAGE )
        {
            crits = 2;
            dealtCrits = true;
        }
        currentHealth -= damage * crits;
        if ( currentHealth <= 0 )
        {
            resetMotion();
            isDead = true;
        }
    }

}
