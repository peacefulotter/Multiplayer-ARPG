package ch.epfl.cs107.play.game.arpg.inventory.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;

public class Heart extends CollectibleAreaEntity {
    private static final int animFrameDuration=8;
    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Heart(Area area, DiscreteCoordinates position) {
        super(area, position);
        RPGSprite[] sprites = new RPGSprite[4];
        for(int i=0; i<4; i++) {
            sprites[i] = new RPGSprite("zelda/heart", 1, 1, this, new RegionOfInterest(i * 16, 0, 16, 16));
        }
        animation= new Animation(animFrameDuration,sprites,true);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v)
    {
        ((ARPGInteractionVisitor)v).interactWith(this);
    }
}
