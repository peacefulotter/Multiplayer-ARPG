package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.inventory.items.Coin;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class LogMonster extends Monster
{
    private static int MIN_SLEEPING_DURATION = 50;
    private static int MAX_SLEEPING_DURATION = 100;
    private static int maxSteps = 100;
    private int steps;

    // monster states
    private boolean isSleeping;
    private boolean isWakingUp;
    private boolean isAttacking;

    private float time;
    private int idleTime;
    private int idleTimeBound;

    private Animation sleepingAnimation;
    private Animation wakingAnimation;


    public LogMonster(Area area, DiscreteCoordinates coords )
    {
        super(area, Orientation.DOWN,
                new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT}, coords,
                "LogMonster", "zelda/logMonster",
                10, 1, 4, new Vector( -0.5f, 0 ), Vulnerabilities.CLOSE_RANGE, Vulnerabilities.FIRE);
        Sprite[] sleepingAnimationSprites = new Sprite[4];
        for ( int i = 0; i < 4; i++ ) {
            sleepingAnimationSprites[i] = new Sprite("zelda/logMonster.sleeping", 2f, 2f, this, new RegionOfInterest(0, i * 32, 32, 32), Vector.ZERO, 1f, 1);
        }
        sleepingAnimation = new Animation(10, sleepingAnimationSprites, true);

        Sprite[] wakingAnimationSprites = new Sprite[3];
        for ( int i = 0; i < 3; i++ ) {
            sleepingAnimationSprites[i] = new Sprite("zelda/logMonster.wakingUp", 2f, 2f, this, new RegionOfInterest(0, i * 32, 32, 32), new Vector( 0, 0 ), 1f, 1);
        }
        wakingAnimation = new Animation(3, sleepingAnimationSprites, false);

        isSleeping = true;
        isWakingUp = false;
        idleTimeBound = 0;
        idleTime = 0;
        idleTimeBound = 0;
        steps = 0;
        time = 0;
    }

    @Override
    public void update(float deltaTime)
    {
        if ( !isAttacking )
        {
            if ( !isSleeping && !isWakingUp )
            {
                super.update( deltaTime );
            }

            if ( Math.random() < 0.01 && !isSleeping )
            {
                isSleeping = true;
                idleTime = 0;
                idleTimeBound = (int) (MIN_SLEEPING_DURATION + Math.random() * (MAX_SLEEPING_DURATION-MIN_SLEEPING_DURATION));
            }

            if ( isSleeping && idleTime <= idleTimeBound )
            {
                sleepingAnimation.update( deltaTime );
                idleTime++;
            } else if ( isSleeping )
            {
                isSleeping = false;
                isWakingUp = true;
                sleepingAnimation.reset();
            }

            else if ( isWakingUp && !wakingAnimation.isCompleted() )
            {
                wakingAnimation.update( deltaTime );
            }
            else if ( isWakingUp && wakingAnimation.isCompleted() )
            {
                isWakingUp = false;
                wakingAnimation.reset();
            }

        }

        else if ( isAttacking )
        {
            super.move( 4 );
            // Monster failed to attack
            if ( steps >= maxSteps )
            {
                isAttacking = false;
                steps = 0;
            }
            steps++;
        }


        if ( deathAnimation.isCompleted() )
        {
            new Coin( getOwnerArea(), getCurrentCells().get(0), 50 );
        }


        // change monster state every 300;
        if ( time >= 300 )
        {
            if ( !isSleeping && !isWakingUp && !isAttacking )
            {
                isAttacking = !isAttacking;
                time = 0;
            }
        }
        time++;
    }

    @Override
    public void draw(Canvas canvas)
    {
        if ( isSleeping )
        {
            sleepingAnimation.setAnchor( new Vector( -0.5f, 0 ) );
            sleepingAnimation.draw( canvas );
        } else if ( isWakingUp )
        {
            wakingAnimation.draw( canvas );
        }
        else
        {
            super.draw(canvas);
        }
    }

    @Override
    public boolean takeCellSpace()
    {
        return true;
    }

    @Override
    public boolean isCellInteractable()
    {
        return false;
    }

    @Override
    public boolean isViewInteractable()
    {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v)
    {
        ((ARPGInteractionVisitor) v).interactWith(this);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells()
    {
        return getNextCurrentCells();
    }

    @Override
    public boolean wantsCellInteraction()
    {
        return false;
    }

    @Override
    public boolean wantsViewInteraction()
    {
        return true;
    }
}
