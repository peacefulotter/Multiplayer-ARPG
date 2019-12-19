package ch.epfl.cs107.play.game.arpg.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.monster.FlyableEntity;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

import java.util.Collections;
import java.util.List;

public abstract class Projectile extends MovableAreaEntity implements Interactor, FlyableEntity {

    private final int speed;

    protected int getSpeed() {
        return speed;
    }

    protected float getMaxDistance() {
        return maxDistance;
    }

    private final float maxDistance;
    private final Vector startingPos;

    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    Projectile(Area area, Orientation orientation, DiscreteCoordinates position, int speed, float maxDistance) {
        super(area, orientation, position);
        this.speed = speed;
        this.maxDistance = maxDistance;
        startingPos=getPosition();
    }

    protected void stopProjectile() {
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector currentPos= getPosition().mul(-1);
        //effectively subtract the current position from starting position to get travel vector
        if(currentPos.add(startingPos).getLength()>maxDistance)stopProjectile();
        move(speed);
        //if projectile encouters a wall or impassable object
        if(!isDisplacementOccurs()){
            stopProjectile();
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public void interactWith(Interactable other) {

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
    public void acceptInteraction(AreaInteractionVisitor v) {

    }

}
