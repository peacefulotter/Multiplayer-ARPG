package ch.epfl.cs107.play.game.arpg.inventory.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class CastleKey extends CollectibleAreaEntity{
    /**
     * Default AreaEntity constructor
     *
     * @param area     (Area): Owner area. Not null
     * @param position (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public CastleKey(Area area, DiscreteCoordinates position) {
        super(area, position);
        animation= new Animation(1, new Sprite[]{new Sprite("zelda/key",1,1,this)});
    }

}
