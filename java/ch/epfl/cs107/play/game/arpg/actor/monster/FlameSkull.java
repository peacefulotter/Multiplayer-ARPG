package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.List;

public class FlameSkull extends Monster implements FlyableEntity
{
    private final float MIN_LIFE_TIME = (float) (Math.random() * 0.3f);
    private final float MAX_LIFE_TIME = 0.5f;

    public FlameSkull( Area area, DiscreteCoordinates coords )
    {
        super(area, Orientation.DOWN, coords,
                "FlameSkull", "zelda/flameSkull",
                3f, Vulnerabilities.LONG_RANGE, Vulnerabilities.MAGIC );
    }

    @Override
    public boolean takeCellSpace()
    {
        return super.isDead;
    }

    @Override
    public boolean isCellInteractable()
    {
        return !super.isDead;
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
        return true;
    }

    @Override
    public boolean wantsViewInteraction()
    {
        return true;
    }
}