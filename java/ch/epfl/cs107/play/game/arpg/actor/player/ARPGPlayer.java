package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGInventory;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGItem;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Mouse;

import java.util.Collections;
import java.util.List;

public class ARPGPlayer extends Player {
    /// Animation duration in frame number
    private final static int ANIMATION_DURATION = 8;
    private final ARPGPlayerHandler handler;

    private float hp;
    private Animation[] animations;
    private int currentAnimation = 2;
    private boolean wantsInteraction = false;


    private ARPGItem currentItem;
    private ARPGInventory inventory;

    private ARPGPlayerStatusGUI playerGUI;

    /**
     * Default Player constructor
     *
     * @param area        (Area): Owner Area, not null
     * @param orientation (Orientation): Initial player orientation, not null
     * @param coordinates (Coordinates): Initial position, not null
     */
    public ARPGPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates) {
        super(area, orientation, coordinates);
        handler = new ARPGPlayerHandler();
        hp = 3;
        Sprite[][] sprites = RPGSprite.extractSprites("zelda/player",
                4, 1, 2,
                this, 16, 32, new Orientation[]{Orientation.DOWN,
                        Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
        animations= RPGSprite.createAnimations(ANIMATION_DURATION/2, sprites);

        inventory = new ARPGInventory(this, 100, 10);
        inventory.addItemToInventory( ARPGItem.BOMB, 3);
        inventory.addItemToInventory(ARPGItem.SWORD);
        inventory.addItemToInventory(ARPGItem.BOW);
    }

    public void update(float deltaTime) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Mouse mouse = getOwnerArea().getMouse();

        // register movement
        moveOrientate(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveOrientate(Orientation.UP, keyboard.get(Keyboard.UP));
        moveOrientate(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveOrientate(Orientation.DOWN, keyboard.get(Keyboard.DOWN));

        // display animation if player is moving
        if( isDisplacementOccurs() )
        {
            animations[currentAnimation].update(deltaTime);
        }
        wantsInteraction = false;
        for(PlayerInput input : PlayerInput.values()){
            if(keyboard.get(input.getKeyCode()).isPressed()) reactToInput(input);
        }
        super.update(deltaTime);
    }
    private void reactToInput(PlayerInput input){
       switch (input){
           case INTERACT: wantsInteraction=true;
           case SHOW_INV: inventory.toggleDisplay();
           case NEXT_ITEM: takeNextItem();
           case USE_ITEM: useItem();
       }
    }
    private void useItem(){
        if(inventory.getCurrentItem() == ARPGItem.BOMB){
            getOwnerArea().registerActor(new Bomb(getOwnerArea(),Orientation.DOWN,getCurrentMainCellCoordinates()));
            inventory.removeItemFromInventory(ARPGItem.BOMB);
        }
    }

    private void takeNextItem()
    {
        currentItem = (ARPGItem)inventory.getNextItem(1);
        playerGUI.setItemSprite(currentItem);
        System.out.println("player got the next item");
    }
    public ARPGItem getEquippedItem(){
        return (ARPGItem)inventory.getCurrentItem();
    }
    @Override
    public void draw(Canvas canvas) {
        if(playerGUI == null) playerGUI= new ARPGPlayerStatusGUI(canvas,this);
        playerGUI.draw(canvas);
        animations[currentAnimation].draw(canvas);
        //message.draw(canvas);
    }

    /**
     * Orientate or Move this player in the given orientation if the given button is down
     *
     * @param orientation (Orientation): given orientation, not null
     * @param btn         (Button): button corresponding to the given orientation, not null
     */
    private void moveOrientate(Orientation orientation, Button btn) {
        if (btn.isDown()) {
            if (getOrientation() == orientation) {
                move(ANIMATION_DURATION);
            } else {
                boolean orientationSuccessful = orientate(orientation);
                if(orientationSuccessful){
                    switch(orientation){
                        case UP: currentAnimation=0;
                            break;
                        case DOWN: currentAnimation=2;
                            break;
                        case LEFT:  currentAnimation=3;
                            break;
                        case RIGHT: currentAnimation=1;
                            break;
                    }
                    animations[currentAnimation].reset();
                }

            }
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return wantsInteraction;
    }


    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }


    @Override
    public void acceptInteraction( AreaInteractionVisitor v )
    {
        System.out.println(v.toString());
        // to do
    }

    @Override
    public void interactWith( Interactable other )
    {
        other.acceptInteraction( handler );
    }

    class ARPGPlayerHandler implements ARPGInteractionVisitor
    {
        @Override
        public void interactWith( Door door )
        {
            if ( door.isOpen() )
            {
                setIsPassingADoor( door );
            }
        }
        @Override
        public void interactWith( Grass grass )
        {
            grass.cutGrass();
        }
    }
}
