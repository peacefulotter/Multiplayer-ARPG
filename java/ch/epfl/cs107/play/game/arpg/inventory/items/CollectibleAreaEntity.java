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
    protected Animation animation;
    private boolean collected=false;
    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public CollectibleAreaEntity(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
    }



    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return new ArrayList<DiscreteCoordinates>(Collections.singleton(getCurrentMainCellCoordinates()));
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

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
    }

    //boolean collected necessary because otherwise item may be collected multiple times before actor is unregistered
    //may actually not be necessary though
    public boolean collect(){
        if(!collected){
            getOwnerArea().unregisterActor(this);
            collected=true;
            return true;
        }
        return false;
    }
}
