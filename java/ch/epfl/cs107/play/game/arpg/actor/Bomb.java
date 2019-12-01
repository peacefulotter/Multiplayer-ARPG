package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Bomb extends Entity implements Interactor {

    private Sprite bombSprite;
    private float fuseTime = 5f;
    /**
     * Default Entity constructor
     *
     * @param position (Coordinate): Initial position of the entity. Not null
     */
    public Bomb(Vector position) {
        super(position);
        bombSprite= new Sprite("zelda/Bomb",1,1f,this);
    }

    @Override
    public void draw(Canvas canvas) {
        bombSprite.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        fuseTime-=deltaTime;
        System.out.println("BOOM");
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return null;
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public void interactWith(Interactable other) {

    }
}
