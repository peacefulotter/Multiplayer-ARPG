package ch.epfl.cs107.play.game.arpg.inventory;


import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.inventory.InventoryItem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import java.util.List;

public class ARPGItem extends AreaEntity
{
    private Sprite sprite;
    private ARPGItems item;
    private List<DiscreteCoordinates> currentCells;

    protected ARPGItem( ARPGItems item, Area area, Orientation orientation, DiscreteCoordinates coords )
    {
        super( area, orientation, coords );
        currentCells.add( coords );
        sprite = new Sprite( item.spriteName, 1f, 1f, this  );
        this.item = item;
    }


    public void giveItem( InventoryItem item )
    {

    }

    public void dropItem( InventoryItem item )
    {

    }

    @Override
    public void draw( Canvas canvas )
    {

    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells()
    {
        return currentCells;
    }

    @Override
    public boolean takeCellSpace()
    {
        return false;
    }

    @Override
    public boolean isCellInteractable()
    {
        return true;
    }

    @Override
    public boolean isViewInteractable()
    {
        return false;
    }

    @Override
    public void acceptInteraction( AreaInteractionVisitor v )
    {

    }
}
