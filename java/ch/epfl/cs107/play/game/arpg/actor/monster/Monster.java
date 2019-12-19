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
    // Minimimum countdown between two attacks
    private static final float ATTACK_COUNTDOWN = 2f;
    // default animation duration
    private static final int ANIMATION_DURATION = 10;
    // the player has a certain chance to deal crits
    private static final double CRITS_PERCENTAGE = 0.2;

    // the sprite of the critical shot
    private final Sprite critsSprite;
    // List of all the vulnerabilities the monster has
    private final List<Vulnerabilities> vulnerabilities;
    private final Animation[] movementAnimation;
    // how many damage the monster can deal
    private final float inflictDamage;
    private float currentHealth;
    private float timeAttack;
    // did the monster has received a critical hit
    private boolean dealtCrits;
    boolean hasAttacked;

    int currentAnimationIndex = 2;
    final Animation deathAnimation;
    boolean isDead;

    Monster(
            Area area, DiscreteCoordinates position, Orientation[] orientations,
            String spriteName, float maxHealth, float damage, int nbFrames,
            Vector spriteOffset, Vulnerabilities... vulnerabilities)
    {
        super( area, Orientation.DOWN, position );
        currentHealth = maxHealth;
        isDead = false;
        inflictDamage = damage;
        timeAttack = 0;
        hasAttacked = false;
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

        // the sprite to show when a critical hit is dealt
        critsSprite = new Sprite( "custom/crits", 1.5f, 1.5f, this, new RegionOfInterest( 0, 0, 541, 541 ), new Vector( -0.25f, 0.75f ), 1, 1000 );
        dealtCrits = false;
    }


    public List<Vulnerabilities> getVulnerabilities()
    {
        return vulnerabilities;
    }

    // by default the monster is allowed to reorientate
    @Override
    public void update( float deltaTime )
    {
        update( deltaTime, true );
    }

    void update(float deltaTime, boolean allowReorientation)
    {
        if ( !isDead )
        {
            if ( hasAttacked )
            {
                // the monster cannot attack for a certain time
                if ( timeAttack > ATTACK_COUNTDOWN )
                {
                    // after that time, it can attack again
                    hasAttacked = false;
                    timeAttack = 0;
                }
                timeAttack += deltaTime;
            }
            if ( allowReorientation )
            {
                // get a random orientation
                Orientation newOrientation = getRandomOrientation();
                if ( Math.random() < 0.01 )
                {
                    // try to reorientate
                    boolean orientationSuccessful = orientate( newOrientation );
                    if ( orientationSuccessful )
                    {
                        // and change the animationIndex accordingly
                        changeAnimationIndex( newOrientation );
                    }
                    // finally, move the monster
                    move( ANIMATION_DURATION );
                }
            }
            movementAnimation[currentAnimationIndex].update(deltaTime);
        }
        // if the monster is dead, but the deathanimation is not finished
        else if ( !deathAnimation.isCompleted() )
        {
            deathAnimation.update( deltaTime );
        // if the monster is dead and the deathAnimation is finished
        } else {
            getOwnerArea().unregisterActor( this );
        }

        // if it received crits
        if ( dealtCrits )
        {
            // then fade out progressively the sprite
            critsSprite.setAlpha( critsSprite.getAlpha() - 0.01f );
            // and when it is not visible
            if ( critsSprite.getAlpha() <= 0 )
            {
                // reset the alpha to 1 for the next use and dealtCrits to false
                critsSprite.setAlpha( 1 );
                dealtCrits = false;
            }
        }

        super.update( deltaTime );
    }

    @Override
    protected boolean orientate(Orientation orientation) {
        boolean orientated= super.orientate(orientation);
        if(orientated) changeAnimationIndex(orientation);
        return orientated;
    }

    // by default we draw the monster
    @Override
    public void draw( Canvas canvas )
    {
        draw( canvas, true );
    }

    void draw(Canvas canvas, boolean drawMonster)
    {
        if ( !isDead )
        {
            if ( drawMonster )
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

    /**
     * Change the currentAnimationIndex according to the monster orientation
     * @param newOrientation : the monster new orientation
     */
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

    /**
     * Get a random orientation
     * @return Orientation
     */
    Orientation getRandomOrientation()
    {
        int random = RandomGenerator.getInstance().nextInt( 4 );
        return Orientation.fromInt( random );
    }

    // getter for the damage the monster can deal
    float getDamage()
    {
        return inflictDamage;
    }

    /**
     * Deal damage to the monster by a certain amout
     * @param damage : the amount of damage to deal
     */
    public void giveDamage( float damage )
    {
        // crits is either 1 (deal no crits) or 2 (deal crits)
        float crits = 1;
        // check if the player dealt crits
        if ( Math.random() < CRITS_PERCENTAGE )
        {
            crits = 2;
            dealtCrits = true;
        }
        // reduce the monster health
        currentHealth -= damage * crits;
        // and set him dead if his health is less than 0
        if ( currentHealth <= 0 )
        {
            resetMotion();
            isDead = true;
        }
    }


    @Override
    public List<DiscreteCoordinates> getCurrentCells()
    {
        return Collections.singletonList( getCurrentMainCellCoordinates() );
    }

}
