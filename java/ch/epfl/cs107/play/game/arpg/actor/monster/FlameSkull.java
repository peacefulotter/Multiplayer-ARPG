package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

import java.util.List;

public class FlameSkull extends Monster implements FlyableEntity
{
    private final flameSkullHandler handler;

    private float lifeTime;

    public FlameSkull( Area area, DiscreteCoordinates coords )
    {
        super(area, coords, new Orientation[]{Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT},
                "zelda/flameSkull", 3f, 1f, 3,
                new Vector( -0.5f, 0 ), Vulnerabilities.LONG_RANGE, Vulnerabilities.MAGIC );
        // initialise the time the flameskull will live
        float MAX_LIFE_TIME = 15f;// the flame skull can live a certain time between these constants
        float MIN_LIFE_TIME = 6f;
        lifeTime = (float) (MIN_LIFE_TIME + Math.random() * (MAX_LIFE_TIME - MIN_LIFE_TIME));
        handler = new flameSkullHandler();
    }


    @Override
    public void update(float deltaTime)
    {
        super.update(deltaTime);
        lifeTime -= deltaTime;
        // if its lifetime is 0 then it is considered dead
        if ( lifeTime <= 0 )
        {
            super.isDead = true;
        }
    }

    @Override
    public boolean takeCellSpace()
    {
        return isDead;
    }

    @Override
    public boolean isCellInteractable()
    {
        return !isDead;
    }

    @Override
    public boolean isViewInteractable()
    {
        return false;
    }

    @Override
    public void acceptInteraction( AreaInteractionVisitor v )
    {
        ((ARPGInteractionVisitor)v).interactWith( this );
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells()
    {
        return getNextCurrentCells();
    }

    @Override
    public boolean wantsCellInteraction()
    {
        return (!isDead && !hasAttacked);
    }

    @Override
    public boolean wantsViewInteraction()
    {
        return false;
    }

    @Override
    public void interactWith( Interactable other )
    {
        other.acceptInteraction( handler );
    }


    class flameSkullHandler implements ARPGInteractionVisitor
    {
        @Override
        public void interactWith( ARPGPlayer player )
        {
            if ( !hasAttacked )
            {
                player.giveDamage( getDamage() );
                hasAttacked = true;
            }
        }

        @Override
        public void interactWith( Grass grass )
        {
            grass.cutGrass();
        }

        @Override
        public void interactWith( Monster monster )
        {
            if ( !hasAttacked && monster.getVulnerabilities().contains( Vulnerabilities.FIRE ) )
            {
                monster.giveDamage( getDamage() );
                hasAttacked = true;
            }
        }

        @Override
        public void interactWith( Bomb bomb )
        {
            bomb.explode();
        }
    }
}
