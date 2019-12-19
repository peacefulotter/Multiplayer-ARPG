package ch.epfl.cs107.play.game.arpg.inventory.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CollectibleAreaEntity extends AreaEntity implements Interactable
{
    //used to check if item has already been collected to avoid duplication
    private boolean collected = false;
    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public CollectibleAreaEntity( Area area, DiscreteCoordinates position )
    {
        super( area, Orientation.DOWN, position );
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList( getCurrentMainCellCoordinates() );
    }


    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    //boolean collected necessary because otherwise item may be collected multiple times before actor is unregistered
    public boolean collect(){
        if(!collected){
            getOwnerArea().unregisterActor(this);
            collected=true;
            return true;
        }
        return false;
    }
}
