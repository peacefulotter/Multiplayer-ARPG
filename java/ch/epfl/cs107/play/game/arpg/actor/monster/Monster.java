package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGItem;
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
    private final float PLAYER_DAMAGE;
    private final ARPGMonsterHandler handler;
    private final Orientation[] orientations;

    private final String name;
    private final Sprite sprite;
    private final float maxHealth;
    private float currentHealth;

    private List<DiscreteCoordinates> currentCells;
    protected boolean isDead;
    private boolean isAttacking = false;
    private List<Vulnerabilities> vulnerabilities;
    protected Animation deathAnimation;
    private Animation[] movementAnimation;
    private int currentAnimationIndex = 0;

    public Monster( Area area, Orientation orientation, Orientation[] orientations, DiscreteCoordinates coords, String name, String spriteName, float maxHealth, float damage, int nbFrames, Vector spriteOffset, Vulnerabilities ... vulnerabilities )
    {
        super( area, orientation, coords );
        this.name = name;
        sprite = new Sprite( spriteName, 1f, 1f, this );
        this.maxHealth = maxHealth;
        currentHealth = maxHealth;
        currentCells = new ArrayList<>();
        currentCells.add( coords );
        isDead = false;
        handler = new ARPGMonsterHandler();
        this.orientations = orientations;
        PLAYER_DAMAGE = damage;

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
    }

    @Override
    public void update(float deltaTime)
    {
        if ( !isDead )
        {
            Orientation newOrientation = getRandomOrientation();
            if ( Math.random() < 0.01 )
            {
                    boolean orientationSuccessful = orientate( newOrientation );
                    if ( orientationSuccessful )
                    {
                        changeAnimationIndex( newOrientation );
                    }
                    onMove();
                    move( ANIMATION_DURATION );
            }

            movementAnimation[currentAnimationIndex].update(deltaTime);

        }
        else if ( !deathAnimation.isCompleted() )
        {
            deathAnimation.update( deltaTime );
        } else {
            getOwnerArea().unregisterActor( this );
        }

        super.update( deltaTime );
    }

    protected void onMove()
    {

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

    // implement this inside subclasses
    // and then super.draw
    @Override
    public void draw( Canvas canvas )
    {
        if ( !isDead )
        {
            movementAnimation[currentAnimationIndex].draw(canvas);
        }
        else
        {
            deathAnimation.draw( canvas );
        }
    }

    private Orientation getRandomOrientation()
    {
        int random = RandomGenerator.getInstance().nextInt( 3 );
        return Orientation.fromInt( random );
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells()
    {
        return Collections.singletonList( getCurrentMainCellCoordinates() );
    }

    public void giveDamage( float damage, Vulnerabilities ... vuln )
    {
        float crits = ( Math.random() < CRITS_PERCENTAGE ) ? 2 : 1;
        for ( Vulnerabilities v : vuln )
        {
            // if the monster is vulnerable to the weapon, then
            // deal *1.5 more damage to him
            if ( vulnerabilities.contains( vuln ) )
            {
                giveDamage( damage * 1.5f * crits );
                return;
            }
        }
        giveDamage( damage * crits );
    }

    private void giveDamage( float damage )
    {
        System.out.println("dealt damage"   );
        currentHealth -= damage;
        if ( currentHealth <= 0 )
        {
            isDead = true;
        }
    }

    @Override
    public void interactWith( Interactable other )
    {
        other.acceptInteraction( handler );
    }


    class ARPGMonsterHandler implements ARPGInteractionVisitor
    {
        public void interactWith( ARPGPlayer player )
        {
            player.giveDamage( PLAYER_DAMAGE );
        }

        @Override
        public void interactWith(Grass grass)
        {
            grass.cutGrass();
        }
    }
}
