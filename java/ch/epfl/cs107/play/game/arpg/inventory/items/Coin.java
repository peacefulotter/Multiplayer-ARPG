package ch.epfl.cs107.play.game.arpg.inventory.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Coin extends CollectibleAreaEntity {
    private final static int animFrameDuration=6;
    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Coin(Area area, DiscreteCoordinates position) {
        super(area,position);
        RPGSprite[] sprites = new RPGSprite[4];
        for(int i=0; i<4; i++) {
            sprites[i] = new RPGSprite("zelda/coin", 1, 1, this, new RegionOfInterest(i * 16, 0, 16, 16));
        }
        animation= new Animation(animFrameDuration,sprites,true);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
