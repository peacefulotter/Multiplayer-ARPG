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

// DarkLord Monster,  inherits Monster
public class DarkLord extends Monster
{
    // minimal/maximum time the darklord takes to activate the next spell (firespell or summon)
    private static final float MIN_SPELL_WAIT_DURATION = 1;
    private static final float MAX_SPELL_WAIT_DURATION = 2;
    // The damage that the fire can deal
    private static final float FIRESPELL_DAMAGE = 0.6f;
    // Firespell force = number of cell it can propagate
    private static final int FIRESPELL_FORCE = 4;
    // the radius around him where he can teleport to
    private static final int TELEPORTATION_RADIUS = 7;
    // the number of tries the darklord has to teleport
    private static final int MAX_TELEPORTATION_TRIES = 10;
    // the radius around him where he can see the player
    private static final int FOV = 7;
    // Used to generate random numbers
    private static final Random random = new Random();

    // Darklord's handler to handler interactions
    private final DarkLordHandler handler;
    // Darklord spell animation
    private final Animation[] spellAnimation;

    // Darklord current state
    private DarkLordStates state;
    // the time between he changes state
    private float cycleTime = 0;
    // true if it recently teleported
    private boolean hasTeleported = false;

    // states the darklord can take
    private enum DarkLordStates {
        // does nothing in particular, walks around
        IDLE( false ),
        // throw a firespell in front of him
        ATTACKING( true ),
        // summon a flameskull around him
        SUMMONING( true ),
        // prepare himself to teleport
        INVOKE_TP( true ),
        // teleports
        TELEPORTING( false );

        // do we need to display the spellAnimation (true) or the normal animation (false)
        public final boolean isSpellAnimation;

        DarkLordStates( boolean isSpellAnimation )
        {
            this.isSpellAnimation = isSpellAnimation;
        }
    }

    /**
     *  DarkLord's constructor
     * @param area : the area it is in
     * @param position : the position it is
     */
    public DarkLord( Area area, DiscreteCoordinates position )
    {
        super(area, position, new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT},
                "DarkLord", "zelda/darkLord", 10, 1.2f, 8, new Vector( -0.5f, 0 ),
                Vulnerabilities.MAGIC );
        // create the handler
        this.handler = new DarkLordHandler();
        // create the darklord spell animation
        Sprite[][] spellSprites = RPGSprite.extractSprites( "zelda/darkLord.spell",
                3, 2, 2,
                this, 32, 32, new Vector( -0.5f, 0 ), new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
        spellAnimation = RPGSprite.createAnimations(5, spellSprites, false );
        // by default, it is in IDLE
        state = DarkLordStates.IDLE;
    }


    @Override
    public void update(float deltaTime)
    {
        // depending on his current state it makes something
        switch ( state )
        {
            case IDLE:
                // it's time to change state -> attack / summon
                if ( cycleTime <= 0 )
                {
                    // reset teleportation
                    hasTeleported = false;
                    // and reset the cycle time
                    resetCycleTime();
                    // instead of switching state depending on a random number
                    // it switches state depending on a random boolean
                    if ( random.nextBoolean() )
                    {
                        state = DarkLordStates.ATTACKING;
                    } else
                    {
                        state = DarkLordStates.SUMMONING;
                    }
                    // orientates to a random orientation
                    orientate( getRandomOrientation() );
                }
                // decrease the cycle time
                cycleTime -= deltaTime;
                super.update( deltaTime );
                break;

            case ATTACKING:
                // update the spellAnimation and check if it is finished
                boolean finishedSpellAnimation = updateSpellAnimation(deltaTime);
                // if it is, then it attacks by throwing a fireSpell
                if (finishedSpellAnimation) { throwMagicFlame(); }
                break;

            case SUMMONING:
                // update the spellAnimation and check if it is finished
                finishedSpellAnimation = updateSpellAnimation( deltaTime );
                // if it is, then it attacks by summoning a flameSkull
                if (finishedSpellAnimation) { summonFlameSkull(); }
                break;

            case INVOKE_TP:
                // update the spellAnimation and check if it is finished
                finishedSpellAnimation = updateSpellAnimation( deltaTime );
                // if it is, then set the state to TELEPORTING
                if (finishedSpellAnimation) { state = DarkLordStates.TELEPORTING; }
                break;

            case TELEPORTING:
                // teleport the darklord
                teleport();
                // and since it just teleported, set hasTeleported to true
                hasTeleported = true;
                break;
        }
        // if it is dead and the deathAnimation (it vanished) is completed
        if ( isDead && deathAnimation.isCompleted() )
        {
            // the castlekey spawns and can be taken by the player
            getOwnerArea().registerActor( new CastleKey( getOwnerArea(), getCurrentCells().get(0) ) );
        }
    }

    @Override
    public void draw( Canvas canvas )
    {
        // draw the animation corresponding to its state
        if ( state.isSpellAnimation )
        {
            spellAnimation[ currentAnimationIndex ].draw( canvas );
        } else
        {
            super.draw(canvas);
        }
    }

    /**
     * Update the spellAnimation and check if it is finished
     * @param deltaTime
     * @return true if the spellAnimation is completed, else return false
     */
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


    /**
     * Changes the darklord orientation to make sure he can throw a firespell in front of him (he must not face a wall
     * or anything else that takes the CellSPace
     * @return true if he successfully changed his orientation, else return false
     */
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


    /**
     * Throw a fireSpell in front of him
     */
    private void throwMagicFlame()
    {
        // try to change the darklord orientation to make sure he can throw a firespell
        boolean isCellAvailable = changeOrientation();
        // create a new fireSpell
        FireSpell fireSpell = new FireSpell( getOwnerArea(), getOrientation(), getNextCurrentCells().get( 0 ), FIRESPELL_DAMAGE, FIRESPELL_FORCE );

        // if the darklord did not orientate successfully
        if ( !isCellAvailable )
        {
            // check if the cell in front of him is occupied or not
            isCellAvailable = getOwnerArea().canEnterAreaCells( fireSpell, getNextCurrentCells() );
        }
        // if the cell in front of him is available
        if ( isCellAvailable )
        {
            // reigster the fireSpell
            getOwnerArea().registerActor( fireSpell );
        }
        // the darklord has finished his attack, his state updates to IDLE
        state = DarkLordStates.IDLE;
    }

    /**
     * Summon a flameSkull at a random position around the darklord
     */
    private void summonFlameSkull()
    {
        // get the field of view of the darklord
        List<DiscreteCoordinates> cells = getFieldOfViewCells();
        // pick a random location in that field of view
        DiscreteCoordinates summonPosition = cells.get( random.nextInt(cells.size() - 1 ) );
        // create a new flameSkull
        FlameSkull flameSkull = new FlameSkull( getOwnerArea(), summonPosition );
        // check if the flame skull can spawn at that place
        boolean canSpawn = getOwnerArea().canEnterAreaCells( flameSkull, Collections.singletonList( summonPosition ) );
        // if it can
        if ( canSpawn )
        {
            // register it to the area
            getOwnerArea().registerActor( flameSkull );
        }
        // the darklord has finished his attack, his state updates to IDLE
        state = DarkLordStates.IDLE;
    }

    /**
     * Teleport the DarkLord elsewhere
     */
    private void teleport()
    {
        // get the cell it is in
        DiscreteCoordinates currentCell = getCurrentCells().get( 0 );
        // for the moment, it did not teleport
        boolean teleported = false;
        // check if the darklord can leave its cell
        boolean canLeave = getOwnerArea().leaveAreaCells( this, Collections.singletonList( currentCell ) );
        // if it is the case
        if ( canLeave )
        {
            // try to teleport the darklord and see if it failed or not
            teleported = tryTeleportationCells( currentCell );
        }
        // if it failed
        if ( !teleported )
        {
            // set his position back to where it was
            setCurrentPosition( currentCell.toVector() );
            // and enter the cell where it was
            getOwnerArea().enterAreaCells(this, Collections.singletonList( currentCell ) );
        }
        // the darklord has finished his teleportation, his state updates to IDLE
        state = DarkLordStates.IDLE;
    }

    /**
     * Try to teleport the darklord to another location
     * @param origin : the cell where he began to teleport
     * @return true if it teleported successfully, else return false
     */
    private boolean tryTeleportationCells( DiscreteCoordinates origin )
    {
        // the cell it wants to teleport
        DiscreteCoordinates tpCell;
        // checked if it entered the cell
        boolean entered;
        // make a certain number of try
        for ( int i = 0; i < MAX_TELEPORTATION_TRIES; i++ )
        {
            // get a random position around the origin coordinates
            tpCell = new DiscreteCoordinates( getRandomPos(), getRandomPos() )
                    .jump ( origin.toVector() );
            // check if it entered the cell
            entered = getOwnerArea().enterAreaCells( this, Collections.singletonList( tpCell ) );
            // if it is the case
            if ( entered )
            {
                // set its position to that cell
                setCurrentPosition( tpCell.toVector() );
                return true;
            }
        }
        return false;
    }

    /**
     * Get a random number from -TELEPORTATION_RADIUS to TELEPORTATION_RADIUS
     * @return the number
     */
    private int getRandomPos()
    {
        return random.nextInt( TELEPORTATION_RADIUS * 2 ) - TELEPORTATION_RADIUS;
    }

    /**
     *  Reset the time it changes state
     */
    private void resetCycleTime()
    {
        cycleTime = MIN_SPELL_WAIT_DURATION + random.nextFloat() * (MAX_SPELL_WAIT_DURATION-MIN_SPELL_WAIT_DURATION);
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
        // get the cell it is currently in
        DiscreteCoordinates cell = getCurrentCells().get( 0 );
        // create a new ArrayList to store the cells around him
        List<DiscreteCoordinates> surroundings = new ArrayList<>();
        // iterates from x-FOV to x+FOV
        for ( int i = cell.x - FOV; i < cell.x + FOV; i++ )
        {
            // iterates from y-FOV to y+FOV
            for ( int j = cell.y - FOV; j < cell.y + FOV; j++ )
            {
                // and add the coordinates to the surroundings list
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
            // interacts with the player only if it IDLE and it has not teleported recently
            if ( state == DarkLordStates.IDLE && !hasTeleported )
            {
                resetCycleTime();
                state = DarkLordStates.INVOKE_TP;
            }
        }

    }

}
