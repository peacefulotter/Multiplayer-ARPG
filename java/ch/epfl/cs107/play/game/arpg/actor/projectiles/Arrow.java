package ch.epfl.cs107.play.game.arpg.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.monster.Monster;
import ch.epfl.cs107.play.game.arpg.actor.monster.Vulnerabilities;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Arrow extends Projectile {
    private static Sprite sprite;
    private ArrowInteractionHandler interactionHandler;

    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param speed
     * @param maxDistance
     */

    public Arrow(Area area, Orientation orientation, DiscreteCoordinates position, int speed, int maxDistance) {
        super(area, orientation, position, speed, maxDistance);
        interactionHandler= new ArrowInteractionHandler();
        int spriteOffset = 0;
        switch (orientation) {
            case UP:
                spriteOffset = Directions.UP.value();
                break;
            case DOWN:
                spriteOffset = Directions.DOWN.value();
                break;
            case RIGHT:
                spriteOffset = Directions.RIGHT.value();
                break;
            case LEFT:
                spriteOffset = Directions.LEFT.value();
                break;
        }
        sprite = new Sprite("zelda/arrow", 1, 1, this, new RegionOfInterest(spriteOffset * 32, 0, 32, 32));
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    private enum Directions {
        UP(0),
        RIGHT(1),
        DOWN(2),
        LEFT(3);
        private int directionIndex;

        Directions(int directionIndex) {
            this.directionIndex = directionIndex;
        }

        public int value() {
            return this.directionIndex;
        }

    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(interactionHandler);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this);
    }

    class ArrowInteractionHandler implements ARPGInteractionVisitor{
        @Override
        public void interactWith(Monster monster) {
            stopProjectile();
            monster.giveDamage(.5f, Vulnerabilities.LONG_RANGE);
        }
    }
}
