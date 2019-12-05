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
    private boolean isWakingUp;
    private Animation sleepingAnimation;
    private Animation wakingAnimation;

    public LogMonster(Area area, DiscreteCoordinates coords )
    {
        super(area, Orientation.DOWN,
                new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT}, coords,
                "LogMonster", "zelda/logMonster",
                10, Vulnerabilities.CLOSE_RANGE, Vulnerabilities.FIRE
                );
        Sprite[] sleepingAnimationSprites = new Sprite[4];
        for ( int i = 0; i < 4; i++ ) {
            sleepingAnimationSprites[i] = new Sprite("zelda/logMonster.sleeping", 2f, 2f, this, new RegionOfInterest(0, i * 32, 32, 32), new Vector( 0, 0 ), 1f, 1);
        }
        sleepingAnimation = new Animation(10, sleepingAnimationSprites, true);

        Sprite[] wakingAnimationSprites = new Sprite[3];
        for ( int i = 0; i < 3; i++ ) {
            sleepingAnimationSprites[i] = new Sprite("zelda/logMonster.wakingUp", 2f, 2f, this, new RegionOfInterest(0, i * 32, 32, 32), new Vector( 0, 0 ), 1f, 1);
        }
        wakingAnimation = new Animation(3, sleepingAnimationSprites, false);

        isSleeping = true;
        isWakingUp = false;
    }

    @Override
    public void update(float deltaTime)
    {
        if ( isWakingUp )
        {
            wakingAnimation.update( deltaTime );
            if ( wakingAnimation.isCompleted() )
            {
                wakingAnimation.reset();
                isWakingUp = false;
            }
        }
        else if ( isSleeping )
        {
            sleepingAnimation.update( deltaTime );
        }
        else
        {
            super.update(deltaTime);
        }

        if ( Math.random() < 0.01 && !isWakingUp )
        {
            isSleeping = !isSleeping;
            if ( !isSleeping )
            {
                isWakingUp = true;
            }
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        if ( isWakingUp )
        {
            wakingAnimation.draw( canvas );
        }
        else if ( isSleeping )
        {
            sleepingAnimation.draw( canvas );
        }
        else
        {
            super.draw(canvas);
        }

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
