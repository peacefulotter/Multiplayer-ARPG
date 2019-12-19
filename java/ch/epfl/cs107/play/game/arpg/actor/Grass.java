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
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Grass extends AreaEntity {
    private static final Random random = new Random();
    private final static float GRASS_DEPTH = -100f;
    private final static double DROP_CHANCE = 0.75;
    private final List<DiscreteCoordinates> currentCells;
    private final Sprite sprite = new Sprite(
            "zelda/grass",
            1, 1,
            this, new RegionOfInterest(0, 0, 16, 16), new Vector(0, 0), 1f, GRASS_DEPTH
    );
    private boolean isCut = false;
    private final Animation grassAnimation;

    public Grass(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        currentCells = new ArrayList<>();
        currentCells.add(position);
        Sprite[] animationSprites = new Sprite[4];

        for (int i = 0; i < 4; i++) {
            animationSprites[i] = new Sprite("zelda/grass.sliced", 1.5f, 1.5f, this, new RegionOfInterest(i * 32, 0, 32, 32), Vector.ZERO, 1f, GRASS_DEPTH);
        }

        grassAnimation = new Animation(8, animationSprites, false);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isCut) {
            sprite.draw(canvas);
        } else if (!grassAnimation.isCompleted()) {
            grassAnimation.draw(canvas);
        } else {
            //Unregisters grass only once it has finished its falling leaves animation
            getOwnerArea().unregisterActor(this);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (isCut)
            grassAnimation.update(deltaTime);
    }

    public void cutGrass() {
        if (isCut) {
            return;
        }
        isCut = true;
        if (random.nextBoolean()) {
            //once cut has a 50/50 chance of dropping a collectible of which DROP_CHANCE % will be coins and the rest hearts
            if (random.nextDouble() < DROP_CHANCE) {
                getOwnerArea().registerActor(new Coin(getOwnerArea(), getCurrentMainCellCoordinates(), 50));
            } else {
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
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return !isCut;
    }

    @Override
    public boolean isViewInteractable() {
        return !isCut;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor) v).interactWith(this);
    }

}
