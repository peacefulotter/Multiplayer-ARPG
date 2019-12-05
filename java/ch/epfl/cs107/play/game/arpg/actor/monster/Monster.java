package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
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

public abstract class Monster extends MovableAreaEntity
{
    private final int ANIMATION_DURATION = 10;
    private final float DAMAGE_VULN = 1.5f;
    private final float DAMAGE_BASIC = 0.5f;
    private final double CRITS_PERCENTAGE = 0.2;

    private final String name;
    private final Sprite sprite;
    private final float maxHealth;
    private float currentHealth;

    private List<DiscreteCoordinates> currentCells;
    private boolean isDead;
    private boolean isAttacking = false;
    private List<Vulnerabilities> vulnerabilities;
    private Animation deathAnimation;
    private Animation[] movementAnimation;
    private int currentAnimationIndex = 0;

    public Monster( Area area, Orientation orientation, DiscreteCoordinates coords, String name, String spriteName, float maxHealth, Vulnerabilities ... vulnerabilities )
    {
        super( area, orientation, coords );
        this.name = name;
        sprite = new Sprite( spriteName, 1f, 1f, this );
        this.maxHealth = maxHealth;
        currentHealth = maxHealth;
        currentCells = new ArrayList<>();
        currentCells.add( coords );
        isDead = false;

        this.vulnerabilities = new ArrayList<>();
        Collections.addAll( this.vulnerabilities, vulnerabilities );

        Sprite[] deathAnimationSprites = new Sprite[7];
        for ( int i = 0; i < 7; i++ ) {
            deathAnimationSprites[i] = new Sprite("zelda/vanish", 1f, 1f, this, new RegionOfInterest(i * 32, 0, 32, 32), Vector.ZERO, 1f, 1);
        }
        deathAnimation = new Animation(ANIMATION_DURATION, deathAnimationSprites, false);

        Sprite[][] sprites = RPGSprite.extractSprites( spriteName,
                3, 2, 2,
                this, 32, 32, new Vector(-0.5f, -0.5f), new Orientation[]{Orientation.UP,
                        Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
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
                        switch( newOrientation ){
                            case UP: currentAnimationIndex=0;
                                break;
                            case DOWN: currentAnimationIndex=2;
                                break;
                            case LEFT:  currentAnimationIndex=3;
                                break;
                            case RIGHT: currentAnimationIndex=1;
                                break;
                        }
                    }
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

    @Override
    public boolean takeCellSpace()
    {
        return isDead;
    }

    @Override
    public boolean isCellInteractable()
    {
        return true;
    }

    @Override
    public boolean isViewInteractable()
    {
        return true;
    }

    @Override
    public void acceptInteraction( AreaInteractionVisitor v )
    {

    }

    public void takeDamage( float damage )
    {
        currentHealth -= damage;
        if ( currentHealth <= 0 )
        {
            // handle death here
        }
    }

    class ARPGMonsterHandler implements ARPGInteractionVisitor
    {
        public void interactWith( ARPGItem item )
        {
            // deal critical damage ? if yes, then double the damage
            float crits = ( Math.random() < CRITS_PERCENTAGE ) ? 2 : 1;
            if ( vulnerabilities.contains( item ) )
            {
                takeDamage( DAMAGE_VULN * crits );
            } else
            {
                takeDamage( DAMAGE_BASIC * crits );
            }
        }


        @Override
        public void interactWith(Grass grass)
        {
            grass.cutGrass();
        }
    }
}
