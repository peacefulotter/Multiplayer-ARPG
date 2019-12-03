package ch.epfl.cs107.play.game.arpg.actor.monster;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGItem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Monster extends MovableAreaEntity
{
    private final String name;
    private final Sprite sprite;
    private final float maxHealth;
    private float currentHealth;
    private List<DiscreteCoordinates> currentCells;
    private boolean isDead;

    private List<ARPGItem> vulnerabilities;

    public Monster( Area area, Orientation orientation, DiscreteCoordinates coords, String name, String spriteName, float maxHealth, ARPGItem ...vulnerableItems )
    {
        super( area, orientation, coords );
        this.name = name;
        sprite = new Sprite( spriteName, 1f, 1f, this );
        this.maxHealth = maxHealth;
        currentHealth = maxHealth;
        currentCells.add( coords );
        isDead = false;

        vulnerabilities = new ArrayList<>();
        Collections.addAll( vulnerabilities, vulnerableItems );
    }

    @Override
    public void draw( Canvas canvas )
    {
        if ( !isDead )
        {
            sprite.draw( canvas );
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells()
    {
        return currentCells;
    }

    @Override
    public boolean takeCellSpace()
    {
        return isDead;
    }

    @Override
    public boolean isCellInteractable()
    {
        return true;
    }

    @Override
    public boolean isViewInteractable()
    {
        return true;
    }

    @Override
    public void acceptInteraction( AreaInteractionVisitor v )
    {

    }
}
