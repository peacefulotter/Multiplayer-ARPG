package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPGBehavior;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.inventory.items.Coin;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.image.AreaAveragingScaleFilter;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogMonster extends Monster
{
    private static int MIN_SLEEPING_DURATION = 70;
    private static int MAX_SLEEPING_DURATION = 120;
    private static int maxSteps = 100;
    private final logMonsterHandler handler;
    private int steps;

    // monster states
    private boolean isSleeping;
    private boolean isWakingUp;
    private boolean isAttacking;

    private int idleTime;
    private int idleTimeBound;

    private Animation sleepingAnimation;
    private Animation wakingAnimation;


    public LogMonster(Area area, DiscreteCoordinates coords )
    {
        super(area, Orientation.DOWN,
                new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT}, coords,
                "LogMonster", "zelda/logMonster",
                10, 1f, 4, new Vector( -0.5f, 0 ), Vulnerabilities.CLOSE_RANGE, Vulnerabilities.FIRE);
        Sprite[] sleepingAnimationSprites = new Sprite[4];
        for ( int i = 0; i < 4; i++ ) {
            sleepingAnimationSprites[i] = new Sprite("zelda/logMonster.sleeping", 2f, 2f, this, new RegionOfInterest(0, i * 32, 32, 32), Vector.ZERO, 1f, 1);
        }
        sleepingAnimation = new Animation(10, sleepingAnimationSprites, true);

        Sprite[] wakingAnimationSprites = new Sprite[3];
        for ( int i = 0; i < 3; i++ ) {
            sleepingAnimationSprites[i] = new Sprite("zelda/logMonster.wakingUp", 2f, 2f, this, new RegionOfInterest(0, i * 32, 32, 32), new Vector( 0, 0 ), 1f, 1);
        }
        wakingAnimation = new Animation(30, sleepingAnimationSprites, false);

        handler = new logMonsterHandler();

        isSleeping = false;
        isWakingUp = true;
        isAttacking = false;
        idleTimeBound = 0;
        idleTime = 0;
        steps = 0;
    }

    @Override
    public void update(float deltaTime)
    {
        if ( !isDead )
        {
            if ( !isAttacking )
            {
                if ( !isSleeping && !isWakingUp )
                {
                    super.update( deltaTime );

                    if ( Math.random() < 0.005 )
                    {
                        isSleeping = true;
                        idleTime = 0;
                        idleTimeBound = (int) (MIN_SLEEPING_DURATION + Math.random() * (MAX_SLEEPING_DURATION-MIN_SLEEPING_DURATION));
                    }
                }

                else if ( isSleeping && idleTime <= idleTimeBound )
                {
                    sleepingAnimation.update( deltaTime );
                    idleTime++;
                }
                else if ( isSleeping && idleTime > idleTimeBound )
                {
                    isSleeping = false;
                    isWakingUp = true;
                    sleepingAnimation.reset();
                }

                else if ( isWakingUp && !wakingAnimation.isCompleted() )
                {
                    wakingAnimation.update( deltaTime );
                }
                else
                {
                    isWakingUp = false;
                    wakingAnimation.reset();
                }

            }

            else if ( isAttacking )
            {
                super.move( 13 );
                super.update( deltaTime, false );

                // Monster failed to attack
                if ( steps >= maxSteps )
                {
                    resetAttack();
                }
                steps++;
            }
        }
        else if ( isDead )
        {
            if ( deathAnimation.isCompleted() )
            {
                System.out.println("coin");
                getOwnerArea().registerActor(
                        new Coin( getOwnerArea(), getCurrentCells().get(0), 50 ) );
            }
            super.update( deltaTime );
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        if ( isWakingUp )
        {
           wakingAnimation.draw( canvas );
        }
        else if ( isSleeping )
        {
            sleepingAnimation.setAnchor( new Vector( -0.5f, 0 ) );
            sleepingAnimation.draw( canvas );
        }
        else
        {
            super.draw(canvas);
        }
    }

    public void resetAttack()
    {
        isAttacking = false;
        isSleeping = true;
        steps = 0;
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
        DiscreteCoordinates cell = getCurrentCells().get(0);
        List<DiscreteCoordinates> viewCells = new ArrayList<>();
        int x = 0; int y = 0;

        switch ( getOrientation() )
        {
            case UP:
                y = 1;
                break;
            case DOWN:
                y = -1;
                break;
            case LEFT:
                x = -1;
                break;
            case RIGHT:
                x = 1;
                break;
        }

        for ( int i = 0; i < 4; i++ )
        {
            viewCells.add( new DiscreteCoordinates( cell.x + (i*x), cell.y + (i*y) ) );
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
        return (!isSleeping && !isWakingUp);
    }

    @Override
    public void interactWith( Interactable other )
    {
        other.acceptInteraction( handler );
    }

    class logMonsterHandler implements ARPGInteractionVisitor
    {
        @Override
        public void interactWith( ARPGPlayer player )
        {
            // if the monster is attacking and a player is right next to him
            if ( isAttacking && !isSleeping && getNextCurrentCells().contains( player.getCurrentCells().get(0) ) )
            {
                System.out.println("damaged");
                player.giveDamage( PLAYER_DAMAGE );
                resetAttack();
            }
            // else, the monster saw a player and starts to attack him
            else if ( !isAttacking )
            {
                isAttacking = true;
            }
        }

    }
}
