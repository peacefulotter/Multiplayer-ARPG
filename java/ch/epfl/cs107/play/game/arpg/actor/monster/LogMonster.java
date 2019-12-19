package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.inventory.items.Coin;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LogMonster extends Monster
{
    // the logmonster can sleep for a certain amount of time
    private static final float MIN_SLEEPING_DURATION = 2;
    private static final float MAX_SLEEPING_DURATION = 3;
    // it cannot attack immediately after an attack
    private static final float MAX_TIME_ATTACK = 2;
    private static final int MAX_HP = 4;
    private static final Random random = new Random();

    private final logMonsterHandler handler;
    // the log monster has states that are defined in the enum LogMonsterState
    private LogMonsterState state;

    // check if the logmonster dropped a coin after his death (used to avoid dropping multiple coins)
    private boolean hasDroppedCoin;
    // time since it felt asleep
    private float sleepingTime;
    // maximum time he can sleep (random each time he falls asleep)
    private float sleepingTimeBound;
    // time it attacks
    private float timeAttack;

    private Animation sleepingAnimation;
    private Animation wakingAnimation;

    // the log monster states
    private enum LogMonsterState {
        IS_IDLE( true, true ),
        IS_SLEEPING( false, false ),
        IS_WAKING( false, false ),
        IS_ATTACKING( false, true );

        // the log monster can reorientate
        public final boolean allowReorientation;
        // do we need to draw monster default animation
        public final boolean drawMonster;

        LogMonsterState( boolean allowReorientation, boolean drawMonster )
        {
            this.allowReorientation = allowReorientation;
            this.drawMonster = drawMonster;
        }
    }


    public LogMonster(Area area, DiscreteCoordinates coords )
    {
        super(area, coords, new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT},
                "zelda/logMonster", MAX_HP, 1f, 4,
                new Vector( -0.5f, 0 ), Vulnerabilities.CLOSE_RANGE, Vulnerabilities.FIRE);
        // Sleeping animation
        Sprite[] sleepingAnimationSprites = new Sprite[4];
        for ( int i = 0; i < 4; i++ ) {
            sleepingAnimationSprites[i] = new Sprite("zelda/logMonster.sleeping", 2f, 2f, this, new RegionOfInterest(0, i * 32, 32, 32), new Vector( -0.5f, 0 ), 1, -1000 );
        }
        sleepingAnimation = new Animation(10, sleepingAnimationSprites, true);

        // Waking animation
        Sprite[] wakingAnimationSprites = new Sprite[3];
        for ( int i = 0; i < 3; i++ ) {
            wakingAnimationSprites[i] = new Sprite("zelda/logMonster.wakingUp", 2f, 2f, this, new RegionOfInterest(0, i * 32, 32, 32), new Vector( -0.5f, 0 ), 1, -1000 );
        }
        wakingAnimation = new Animation(10, wakingAnimationSprites, false);

        handler = new logMonsterHandler();
        // the log monster is IDLE at the beginning
        state = LogMonsterState.IS_IDLE;
        timeAttack = 0;
        hasDroppedCoin = false;
    }

    @Override
    public void update(float deltaTime)
    {
        switch( state )
        {
            case IS_IDLE:
                // when IDLE, it has a certain chance to fall asleep
                if ( random.nextFloat() < 0.005 )
                {
                    setSleeping();
                }
                break;

            case IS_SLEEPING:
                // when sleeping, update the animation
                sleepingAnimation.update( deltaTime );
                sleepingTime += deltaTime;
                // if finished sleeping
                if ( sleepingTime >= sleepingTimeBound )
                {
                    // then it is waking
                    state = LogMonsterState.IS_WAKING;
                    // and the animation must be reset (in case it has not finished)
                    sleepingAnimation.reset();
                }
                break;

            case IS_WAKING:
                // if it finished waking up
                if ( wakingAnimation.isCompleted() )
                {
                    // then it goes back to idle
                    state = LogMonsterState.IS_IDLE;
                    // and once again we need to reset the animation
                    wakingAnimation.reset();
                }
                // or it still is not finished to wake up
                else
                {
                    // so we update the animation
                    wakingAnimation.update( deltaTime );
                }
                break;

            case IS_ATTACKING:
                // when attacking, the monster only move straight-forward and fast
                super.move( 13 );
                // if it did not reached its target
                if ( timeAttack >= MAX_TIME_ATTACK )
                {
                    // Reset the attack
                    resetAttack();
                }
                timeAttack += deltaTime;
                break;
        }

        // drop a coin when the logmonster is dead
        if ( isDead && !hasDroppedCoin && deathAnimation.isCompleted() )
        {
            hasDroppedCoin = true;
            getOwnerArea().registerActor(
                    new Coin( getOwnerArea(), getCurrentCells().get(0), 50 ) );
        }
        // update but can only change orientation if the state allows to
        super.update( deltaTime, state.allowReorientation );
    }

    @Override
    public void draw(Canvas canvas)
    {
        // while it is not dead
        if ( !isDead )
        {
            switch( state )
            {
                // either it is waking up
                case IS_WAKING:
                    wakingAnimation.draw( canvas );
                    break;
                 // or sleeping
                case IS_SLEEPING:
                    sleepingAnimation.draw( canvas );
                    break;
            }
        }
        // or we draw only either the default animation or the death animation
        // we let the super.draw handle this since it is common to all monsters
        super.draw( canvas, state.allowReorientation );
    }

    /**
     * Change the logMonster to the state Sleeping
     *  and redefine a new sleeping time bound based on randomness
     */
    public void setSleeping()
    {
        state = LogMonsterState.IS_SLEEPING;
        sleepingTime = 0;
        sleepingTimeBound = MIN_SLEEPING_DURATION + random.nextFloat() * (MAX_SLEEPING_DURATION-MIN_SLEEPING_DURATION);
    }

    /**
     * After an attack, the logMonster falls asleep
     *  and his time it attacked resets to 0
     */
    public void resetAttack()
    {
        setSleeping();
        timeAttack = 0;
    }

    @Override
    public boolean takeCellSpace()
    {
        return !isDead;
    }

    @Override
    public boolean isCellInteractable()
    {
        return !isDead;
    }

    @Override
    public boolean isViewInteractable()
    {
        return !isDead;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v)
    {
        ((ARPGInteractionVisitor) v).interactWith(this);
    }

    /**
     * It's field of view is either 10 cells in front of him if it is IDLE (to spot a player even far away)
     * or 1 cell when attacking to deal damage to the player if it encounters one only in front of him
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells()
    {
        DiscreteCoordinates cell = getCurrentCells().get(0);
        List<DiscreteCoordinates> viewCells = new ArrayList<>();
        Vector orientationVector = getOrientation().toVector();

        int maxView = 0;
        switch ( state )
        {
            case IS_ATTACKING:
                maxView = 1;
                break;
            case IS_IDLE:
                maxView = 10;
        }

        for ( int i = 1; i < maxView+1; i++ )
        {
            viewCells.add( new DiscreteCoordinates( cell.x + (i*(int)orientationVector.x), cell.y + (i*(int)orientationVector.y) ) );
        }
        return viewCells;
    }

    @Override
    public boolean wantsCellInteraction() { return false; }

    @Override
    public boolean wantsViewInteraction()
    {
        return state == LogMonsterState.IS_IDLE || state == LogMonsterState.IS_ATTACKING;
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
            if ( state == LogMonsterState.IS_ATTACKING )
            {
                player.giveDamage( getDamage() );
                resetAttack();
            }
            // else, the monster saw a player and starts to attack him
            else
            {
                state = LogMonsterState.IS_ATTACKING;
            }
        }
    }
}
