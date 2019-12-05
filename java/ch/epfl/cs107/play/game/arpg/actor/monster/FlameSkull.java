package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class FlameSkull extends Monster
{
    private final float MIN_LIFE_TIME = (float) (Math.random() * 0.3f);
    private final float MAX_LIFE_TIME = 0.5f;

    public FlameSkull( Area area, DiscreteCoordinates coords )
    {
        super(area, Orientation.DOWN, coords,
                "FlameSkull", "zelda/flameSkull",
                3f, Vulnerabilities.LONG_RANGE, Vulnerabilities.MAGIC );
    }
}
