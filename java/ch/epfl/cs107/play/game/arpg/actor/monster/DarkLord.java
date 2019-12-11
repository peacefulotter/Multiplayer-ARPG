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
    private static final float MIN_SPELL_WAIT_DURATION = 5;
    private static final float MAX_SPELL_WAIT_DURATION = 10;
    private static final float FIRESPELL_DAMAGE = 0.5f;
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

    private enum DarkLordStates {
        IDLE( false ),
        ATTACKING( true ),
        SUMMONING( true ),
        INVOKE_TP( true ),
        TELEPORTING( false );

        public final boolean isSpellAnimation;

        DarkLordStates( boolean spellAnimation )
        {
            this.isSpellAnimation = spellAnimation;
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
                System.out.println("idle");
                if ( cycleTime >= cycleTimeBound )
                {
                    System.out.println("reset");
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
                    for ( int i = 0; i < 3; i++ )
                    {
                        Orientation newOrientation = getRandomOrientation();
                        boolean isCellAvailable = getOwnerArea().canEnterAreaCells( this, getNextCurrentCells() );
                        if ( newOrientation != getOrientation() && isCellAvailable )
                        {
                            orientate( newOrientation );
                        }
                    }
                    orientate( getRandomOrientation() );
                }
                cycleTime += deltaTime;
                super.update( deltaTime );
                break;
            case ATTACKING:
                if ( spellAnimation[ currentAnimationIndex ].isCompleted() )
                {
                    throwMagicFlame();
                    spellAnimation[ currentAnimationIndex ].reset();
                } else {
                    spellAnimation[ currentAnimationIndex ].update( deltaTime );
                }
                System.out.println("attacking");
                break;
            case SUMMONING:
                if ( spellAnimation[ currentAnimationIndex ].isCompleted() )
                {
                    summonFlameSkull();
                    spellAnimation[ currentAnimationIndex ].reset();
                } else {
                    spellAnimation[ currentAnimationIndex ].update( deltaTime );
                }
                System.out.println("summoning");
                break;
            case INVOKE_TP:
                if ( spellAnimation[ currentAnimationIndex ].isCompleted() )
                {
                    state = DarkLordStates.TELEPORTING;
                    spellAnimation[ currentAnimationIndex ].reset();
                } else {
                    spellAnimation[ currentAnimationIndex ].update( deltaTime );
                }
                break;
            case TELEPORTING:
                System.out.println("teleporting");
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

    private void throwMagicFlame()
    {
        getOwnerArea().registerActor(
                new FireSpell( getOwnerArea(), getOrientation(), getNextCurrentCells().get( 0 ), FIRESPELL_DAMAGE ) );
        state = DarkLordStates.IDLE;
    }

    private void summonFlameSkull()
    {
        List<DiscreteCoordinates> cells = getFieldOfViewCells();
        DiscreteCoordinates summonCoords = cells.get( random.nextInt(cells.size() - 1 ) );
        // CHECK IF SUMMON COORDS CAN BE FILLED WITH A FLAME SKULL
        FlameSkull flameSkull = new FlameSkull( getOwnerArea(), summonCoords );
        boolean canSpawn = getOwnerArea().canEnterAreaCells( flameSkull, Collections.singletonList( summonCoords ) );
        if ( canSpawn )
        {
            getOwnerArea().registerActor( flameSkull );
        }
        state = DarkLordStates.IDLE;
    }

    private void teleport()
    {
        DiscreteCoordinates currentCell = getCurrentCells().get( 0 );
        DiscreteCoordinates tpCell;

        int nbTries = 0;
        boolean teleported;
        do {
            tpCell = new DiscreteCoordinates( getRandomPos(), getRandomPos() );
            tpCell = tpCell.jump( currentCell.toVector() );
            teleported = getOwnerArea().registerActor( new DarkLord( getOwnerArea(), tpCell ) );
            nbTries++;
        } while ( nbTries <= MAX_TELEPORTATION_TRIES && !teleported );

        // if the teleportation worked, unregister the previous darklord
        if ( teleported )
        {
            getOwnerArea().unregisterActor( this );
        }
        state = DarkLordStates.IDLE;
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
    protected void onMove()
    {

    }

    @Override
    public boolean takeCellSpace()
    {
        return !isDead;
    }

    @Override
    public boolean isCellInteractable()
    {
        return false;
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
