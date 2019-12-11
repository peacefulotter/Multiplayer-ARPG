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
import java.util.List;
import java.util.Random;

public class DarkLord extends Monster
{
    private static final float MIN_SPELL_WAIT_DURATION = 200;
    private static final float MAX_SPELL_WAIT_DURATION = 300;
    private static final float FIRESPELL_DAMAGE = 0.5f;
    private static final int TELEPORTATION_RADIUS = 4;
    private static final int MAX_TELEPORTATION_TRIES = 4;
    private static final int FOV = 7;
    private static final Random random = new Random();

    private final DarkLordHandler handler;
    private final Animation[] spellAnimation;

    private DarkLordStates state;
    private float cycleTime = 0;
    private float cycleTimeBound = 50;

    private enum DarkLordStates {
        IDLE(),
        ATTACKING(),
        SUMMONING(),
        INVOKE_TP(),
        TELEPORTING()
    }

    public DarkLord( Area area, DiscreteCoordinates coords )
    {
        super(area, coords, new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT},
                "DarkLord", "zelda/darkLord", 10, 1.2f, 8, new Vector( -0.5f, 0 ),
                Vulnerabilities.MAGIC );
        this.handler = new DarkLordHandler();
        Sprite[][] spellSprites = RPGSprite.extractSprites( "zelda/darkLord.spell",
                3, 2, 2,
                this, 32, 32, Vector.ZERO, new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
        spellAnimation = RPGSprite.createAnimations(5, spellSprites);
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
                    cycleTime = 0;
                    cycleTimeBound = MIN_SPELL_WAIT_DURATION + random.nextFloat() * (MAX_SPELL_WAIT_DURATION-MIN_SPELL_WAIT_DURATION);
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
                        if ( newOrientation != getOrientation() && getNextCurrentCells().get( 0 ).x == 0 )
                        {
                            orientate( newOrientation );
                        }
                    }
                    orientate( getRandomOrientation() );
                }
                cycleTime++;
                break;
            case ATTACKING:
                throwMagicFlame();
                state = DarkLordStates.IDLE;
                break;
            case INVOKE_TP:
                break;
            case SUMMONING:
                summonFlameSkull();
                state = DarkLordStates.IDLE;
                break;
            case TELEPORTING:
                teleport();
                state = DarkLordStates.IDLE;
                break;
        }
        if ( isDead && deathAnimation.isCompleted() )
        {
            getOwnerArea().registerActor( new CastleKey( getOwnerArea(), getCurrentCells().get(0) ) );
        }
        super.update( deltaTime );
    }

    @Override
    public void draw(Canvas canvas)
    {
        if ( state == DarkLordStates.ATTACKING || state == DarkLordStates.SUMMONING )
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
    }

    private void summonFlameSkull()
    {
        List<DiscreteCoordinates> cells = getFieldOfViewCells();
        DiscreteCoordinates summonCoords = cells.get( random.nextInt(cells.size() - 1 ) );
        // CHECK IF SUMMONCOORDS CAN BE FILLED WITH A FLAME SKULL
        new FlameSkull( getOwnerArea(), summonCoords );
    }

    private void teleport()
    {
        Vector currentCell = getCurrentCells().get( 0 ).toVector();
        DiscreteCoordinates tpCell;

        int nbTries = 0;
        do {
            tpCell = new DiscreteCoordinates( getRandomPos(), getRandomPos() );
            nbTries++;
            // CHECK IF CELL IS ACCESSIBLE
        } while ( nbTries <= MAX_TELEPORTATION_TRIES && true );

        currentCell.add( tpCell.toVector() );
        setCurrentPosition( currentCell );
    }

    private int getRandomPos()
    {
        return random.nextInt( TELEPORTATION_RADIUS * 2 ) - TELEPORTATION_RADIUS;
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
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v)
    {
        ((ARPGInteractionVisitor)v).interactWith( this );
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
        List<DiscreteCoordinates>surroundings = new ArrayList<>();
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
        return true;
    }

    class DarkLordHandler implements ARPGInteractionVisitor
    {
        @Override
        public void interactWith( ARPGPlayer player )
        {
            //state = DarkLordStates.ATTACKING;
        }
    }

}
