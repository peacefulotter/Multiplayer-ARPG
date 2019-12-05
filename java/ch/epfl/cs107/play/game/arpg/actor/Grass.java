package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.inventory.items.Coin;
import ch.epfl.cs107.play.game.arpg.inventory.items.Heart;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Grass extends AreaEntity {
    private final List<DiscreteCoordinates> currentCells;
    private static final Random random= new Random();
    public boolean isCut = false;
    private float grassDepth = -100f;
    private final Sprite sprite = new Sprite(
            "zelda/grass",
            1, 1,
            this, new RegionOfInterest(0, 0, 16, 16), new Vector(0, 0), 1f, grassDepth
    );
    private Animation grassAnimation;

    public Grass(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        currentCells = new ArrayList<>();
        currentCells.add(position);
        Sprite[] animationSprites = new Sprite[4];

        for (int i = 0; i < 4; i++) {
            animationSprites[i] = new Sprite("zelda/grass.sliced", 1f, 1f, this, new RegionOfInterest(i * 32, 0, 32, 32), Vector.ZERO, 1f, grassDepth);
        }

        grassAnimation = new Animation(8, animationSprites, false);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isCut) {
            sprite.draw(canvas);
        } else if(!grassAnimation.isCompleted()){
            grassAnimation.draw(canvas);
        } else {
            getOwnerArea().unregisterActor(this);
        }
    }

    @Override
    public void update(float deltaTime) {
        if(isCut)
            grassAnimation.update(deltaTime);
    }

    public void cutGrass() {
        if(isCut) return;
        isCut = true;
        if(random.nextBoolean()){
            if(random.nextDouble()<.75){
                getOwnerArea().registerActor(new Coin(getOwnerArea(), getCurrentMainCellCoordinates(), 50));
            }else{
                getOwnerArea().registerActor(new Heart(getOwnerArea(), getCurrentMainCellCoordinates()));
            }
        }
    }


    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return currentCells;
    }

    @Override
    public boolean takeCellSpace() {
        return !isCut;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor) v).interactWith(this);
    }

}
