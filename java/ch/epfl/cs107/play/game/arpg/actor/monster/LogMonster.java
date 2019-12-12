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
    private static final float MIN_SLEEPING_DURATION = 2;
    private static final float MAX_SLEEPING_DURATION = 3;
    private static final float MAX_TIME_ATTACK = 2;
    private static final int MAX_HP = 4;
    private static final Random random = new Random();

    private final logMonsterHandler handler;
    private LogMonsterState state;

    private boolean hasDroppedCoin;
    private float sleepingTime;
    private float sleepingTimeBound;
    private float timeAttack;

    private Animation sleepingAnimation;
    private Animation wakingAnimation;


    private enum LogMonsterState {
        IS_IDLE( true, true ),
        IS_SLEEPING( false, false ),
        IS_WAKING( false, false ),
        IS_ATTACKING( false, true );

        public final boolean allowReorientation;
        public final boolean drawPlayer;

        LogMonsterState( boolean allowReorientation, boolean drawPlayer )
        {
            this.allowReorientation = allowReorientation;
            this.drawPlayer = drawPlayer;
        }
    }


    public LogMonster(Area area, DiscreteCoordinates coords )
    {
        super(area, coords, new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT},
                "LogMonster", "zelda/logMonster",
                MAX_HP, 1f, 4, new Vector( -0.5f, 0 ), Vulnerabilities.CLOSE_RANGE, Vulnerabilities.FIRE);
        Sprite[] sleepingAnimationSprites = new Sprite[4];
        for ( int i = 0; i < 4; i++ ) {
            sleepingAnimationSprites[i] = new Sprite("zelda/logMonster.sleeping", 2f, 2f, this, new RegionOfInterest(0, i * 32, 32, 32), new Vector( -0.5f, 0 ), 1, -1000 );
        }
        sleepingAnimation = new Animation(10, sleepingAnimationSprites, true);

        Sprite[] wakingAnimationSprites = new Sprite[3];
        for ( int i = 0; i < 3; i++ ) {
            wakingAnimationSprites[i] = new Sprite("zelda/logMonster.wakingUp", 2f, 2f, this, new RegionOfInterest(0, i * 32, 32, 32), new Vector( -0.5f, 0 ), 1, -1000 );
        }
        wakingAnimation = new Animation(10, wakingAnimationSprites, false);

        handler = new logMonsterHandler();
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
                if ( random.nextFloat() < 0.005 )
                {
                    setSleeping();
                }
                break;

            case IS_SLEEPING:
                sleepingAnimation.update( deltaTime );
                sleepingTime += deltaTime;
                if ( sleepingTime >= sleepingTimeBound )
                {
                    state = LogMonsterState.IS_WAKING;
                    sleepingAnimation.reset();
                }
                break;

            case IS_WAKING:
                if ( wakingAnimation.isCompleted() )
                {
                    state = LogMonsterState.IS_IDLE;
                    wakingAnimation.reset();
                }
                else
                {
                    wakingAnimation.update( deltaTime );
                }
                break;

            case IS_ATTACKING:
                super.move( 13 );
                if ( timeAttack >= MAX_TIME_ATTACK )
                {
                    resetAttack();
                }
                timeAttack += deltaTime;
                break;
        }

        if ( isDead && !hasDroppedCoin )
        {
            if ( deathAnimation.isCompleted() )
            {
                hasDroppedCoin = true;
                getOwnerArea().registerActor(
                        new Coin( getOwnerArea(), getCurrentCells().get(0), 50 ) );
            }
        }

        super.update( deltaTime, state.allowReorientation );
    }

    @Override
    public void draw(Canvas canvas)
    {
        if ( !isDead )
        {
            switch( state )
            {
                case IS_WAKING:
                    wakingAnimation.draw( canvas );
                    break;
                case IS_SLEEPING:
                    sleepingAnimation.draw( canvas );
                    break;
            }
        }
        super.draw( canvas, state.drawPlayer );
    }

    public void setSleeping()
    {
        state = LogMonsterState.IS_SLEEPING;
        sleepingTime = 0;
        sleepingTimeBound = MIN_SLEEPING_DURATION + random.nextFloat() * (MAX_SLEEPING_DURATION-MIN_SLEEPING_DURATION);
    }

    public void resetAttack()
    {
        setSleeping();
        timeAttack = 0;
    }

    public void onMove() {}

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
    public boolean wantsCellInteraction()
    {
        return false;
    }

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
                player.giveDamage( PLAYER_DAMAGE );
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
