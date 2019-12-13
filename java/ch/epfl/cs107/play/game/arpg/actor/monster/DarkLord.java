package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.inventory.items.CastleKey;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DarkLord extends Monster
{
    private static final float MIN_SPELL_WAIT_DURATION = 1;
    private static final float MAX_SPELL_WAIT_DURATION = 2;
    private static final float FIRESPELL_DAMAGE = 0.6f;
    private static final int FIRESPELL_FORCE = 4;
    private static final int TELEPORTATION_RADIUS = 7;
    private static final int MAX_TELEPORTATION_TRIES = 10;
    private static final int FOV = 7;
    private static final Random random = new Random();

    private final DarkLordHandler handler;
    private final Animation[] spellAnimation;

    private DarkLordStates state;
    private float cycleTime = 0;
    private float cycleTimeBound = 2;
    private boolean hasTeleported = false;
    private boolean finishedSpellAnimation;

    private enum DarkLordStates {
        IDLE( false ),
        ATTACKING( true ),
        SUMMONING( true ),
        INVOKE_TP( true ),
        TELEPORTING( false );

        public final boolean isSpellAnimation;

        DarkLordStates( boolean isSpellAnimation )
        {
            this.isSpellAnimation = isSpellAnimation;
        }
    }

    public DarkLord( Area area, DiscreteCoordinates coords )
    {
        super(area, coords, new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT},
                "DarkLord", "zelda/darkLord", 10, 1.2f, 8, new Vector( -0.5f, 0 ),
                Vulnerabilities.MAGIC );
        this.handler = new DarkLordHandler();
        Sprite[][] spellSprites = RPGSprite.extractSprites( "zelda/darkLord.spell",
                3, 2, 2,
                this, 32, 32, new Vector( -0.5f, 0 ), new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
        spellAnimation = RPGSprite.createAnimations(5, spellSprites, false );
        state = DarkLordStates.IDLE;
    }


    @Override
    public void update(float deltaTime)
    {
        switch ( state )
        {
            case IDLE:
                if ( cycleTime >= cycleTimeBound )
                {
                    hasTeleported = false;
                    resetCycleTime();
                    // instead of switching state depending on a random number
                    // it switches state depending on a boolean
                    if ( random.nextBoolean() )
                    {
                        state = DarkLordStates.ATTACKING;
                    } else
                    {
                        state = DarkLordStates.SUMMONING;
                    }
                    orientate( getRandomOrientation() );
                }
                cycleTime += deltaTime;
                super.update( deltaTime );
                break;

            case ATTACKING:
                finishedSpellAnimation = updateSpellAnimation( deltaTime );
                if ( finishedSpellAnimation ) { throwMagicFlame(); }
                break;

            case SUMMONING:
                finishedSpellAnimation = updateSpellAnimation( deltaTime );
                if ( finishedSpellAnimation ) { summonFlameSkull(); }
                break;

            case INVOKE_TP:
                finishedSpellAnimation = updateSpellAnimation( deltaTime );
                if ( finishedSpellAnimation ) { state = DarkLordStates.TELEPORTING; }
                break;

            case TELEPORTING:
                teleport();
                hasTeleported = true;
                break;
        }
        if ( isDead && deathAnimation.isCompleted() )
        {
            getOwnerArea().registerActor( new CastleKey( getOwnerArea(), getCurrentCells().get(0) ) );
        }
    }

    @Override
    public void draw( Canvas canvas )
    {
        if ( state.isSpellAnimation )
        {
            spellAnimation[ currentAnimationIndex ].draw( canvas );
        } else
        {
            super.draw(canvas);
        }
    }

    private boolean updateSpellAnimation( float deltaTime )
    {
        if ( spellAnimation[ currentAnimationIndex ].isCompleted() )
        {
            spellAnimation[ currentAnimationIndex ].reset();
            return true;
        } else {
            spellAnimation[ currentAnimationIndex ].update( deltaTime );
            return false;
        }
    }

    private boolean changeOrientation()
    {
        for ( int i = 0; i < 4; i++ )
        {
            Orientation newOrientation = getRandomOrientation();
            boolean isCellAvailable = getOwnerArea().canEnterAreaCells( this, getNextCurrentCells() );
            if ( newOrientation != getOrientation() && isCellAvailable )
            {
                orientate( newOrientation );
                return true;
            }
        }
        return false;
    }

    private void throwMagicFlame()
    {
        // try to change the darklord orientation to make sure he can throw a firespell
        boolean isCellAvailable = changeOrientation();
        FireSpell fireSpell = new FireSpell( getOwnerArea(), getOrientation(), getNextCurrentCells().get( 0 ), FIRESPELL_DAMAGE, FIRESPELL_FORCE );
        if ( !isCellAvailable )
        {
            isCellAvailable = getOwnerArea().canEnterAreaCells( fireSpell, getNextCurrentCells() );
        }
        if ( isCellAvailable )
        {
            getOwnerArea().registerActor( fireSpell );
        }
        getOwnerArea().registerActor( fireSpell );
        state = DarkLordStates.IDLE;
    }

    private void summonFlameSkull()
    {
        List<DiscreteCoordinates> cells = getFieldOfViewCells();
        DiscreteCoordinates summonPosition = cells.get( random.nextInt(cells.size() - 1 ) );
        FlameSkull flameSkull = new FlameSkull( getOwnerArea(), summonPosition );
        boolean canSpawn = getOwnerArea().canEnterAreaCells( flameSkull, Collections.singletonList( summonPosition ) );
        if ( canSpawn )
        {
            getOwnerArea().registerActor( flameSkull );
        }
        state = DarkLordStates.IDLE;
    }

    private void teleport()
    {
        DiscreteCoordinates currentCell = getCurrentCells().get( 0 );

        boolean teleported = false;
        boolean canLeave = getOwnerArea().leaveAreaCells( this, Collections.singletonList( currentCell ) );
        if ( canLeave )
        {
            teleported = tryTeleportationCells( currentCell );
        }
        if ( !teleported )
        {
            setCurrentPosition( currentCell.toVector() );
            getOwnerArea().enterAreaCells(this, Collections.singletonList( currentCell ) );
        }
        state = DarkLordStates.IDLE;
    }

    private boolean tryTeleportationCells( DiscreteCoordinates origin )
    {
        DiscreteCoordinates tpCell;
        boolean entered;
        for ( int i = 0; i < MAX_TELEPORTATION_TRIES; i++ )
        {
            tpCell = new DiscreteCoordinates( getRandomPos(), getRandomPos() )
                    .jump ( origin.toVector() );
            entered = getOwnerArea().enterAreaCells( this, Collections.singletonList( tpCell ) );
            if ( entered )
            {
                setCurrentPosition( tpCell.toVector() );
                return true;
            }
        }
        return false;
    }

    private int getRandomPos()
    {
        return random.nextInt( TELEPORTATION_RADIUS * 2 ) - TELEPORTATION_RADIUS;
    }


    private void resetCycleTime()
    {
        cycleTime = 0;
        cycleTimeBound = MIN_SPELL_WAIT_DURATION + random.nextFloat() * (MAX_SPELL_WAIT_DURATION-MIN_SPELL_WAIT_DURATION);
    }


    @Override
    public boolean takeCellSpace()
    {
        return !isDead;
    }

    @Override
    public boolean isCellInteractable()
    {
        return true;
    }

    @Override
    public boolean isViewInteractable()
    {
        return !isDead;
    }

    @Override
    public void acceptInteraction( AreaInteractionVisitor v )
    {
        ( (ARPGInteractionVisitor)v ).interactWith( this );
    }

    @Override
    public void interactWith( Interactable other )
    {
        other.acceptInteraction( handler );
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells()
    {
        DiscreteCoordinates cell = getCurrentCells().get( 0 );
        List<DiscreteCoordinates> surroundings = new ArrayList<>();
        for ( int i = cell.x - FOV; i < cell.x + FOV; i++ )
        {
            for ( int j = cell.y - FOV; j < cell.y + FOV; j++ )
            {
                surroundings.add( new DiscreteCoordinates( i, j ) );
            }
        }
        return surroundings;
    }

    @Override
    public boolean wantsCellInteraction()
    {
        return false;
    }

    @Override
    public boolean wantsViewInteraction()
    {
        return !isDead;
    }

    class DarkLordHandler implements ARPGInteractionVisitor
    {
        @Override
        public void interactWith( ARPGPlayer player )
        {
            if ( state == DarkLordStates.IDLE && !hasTeleported )
            {
                resetCycleTime();
                state = DarkLordStates.INVOKE_TP;
            }
        }

    }

}
