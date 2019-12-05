package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class LogMonster extends Monster
{
    private boolean isSleeping;
    private Animation sleepingAnimation;

    public LogMonster(Area area, Orientation orientation, DiscreteCoordinates coords, String name, String spriteName, float maxHealth, Vulnerabilities... vulnerabilities)
    {
        super(area, orientation, coords,
                "LogMonster", "zelda/logMonster",
                10, Vulnerabilities.CLOSE_RANGE, Vulnerabilities.FIRE
                );
        Sprite[] sleepingAnimationSprites = new Sprite[7];
        for ( int i = 0; i < 7; i++ ) {
            sleepingAnimationSprites[i] = new Sprite("zelda/vanish", 1f, 1f, this, new RegionOfInterest(i * 32, 0, 32, 32), Vector.ZERO, 1f, 1);
        }
        sleepingAnimation = new Animation(7, sleepingAnimationSprites, false);

        isSleeping = false;
    }

    @Override
    public void update(float deltaTime)
    {
        if ( isSleeping )
        {

        }
        if ( !isSleeping )
        {
            super.update(deltaTime);
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
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

    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells()
    {
        return null;
    }

    @Override
    public boolean wantsCellInteraction()
    {
        return false;
    }

    @Override
    public boolean wantsViewInteraction()
    {
        return false;
    }
}
