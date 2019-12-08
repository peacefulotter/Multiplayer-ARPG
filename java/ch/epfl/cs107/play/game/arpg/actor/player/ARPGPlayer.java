package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.CastleDoor;
import ch.epfl.cs107.play.game.arpg.actor.monster.FlameSkull;
import ch.epfl.cs107.play.game.arpg.actor.monster.Monster;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGInventory;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGItem;
import ch.epfl.cs107.play.game.arpg.inventory.items.CastleKey;
import ch.epfl.cs107.play.game.arpg.inventory.items.Coin;
import ch.epfl.cs107.play.game.arpg.inventory.items.CollectibleAreaEntity;
import ch.epfl.cs107.play.game.arpg.inventory.items.Heart;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
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

    private PlayerStates state;
    private float hp;
    private int maxHP = 3;
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
        state = PlayerStates.IDLE;
        handler = new ARPGPlayerHandler();
        hp = maxHP;

        Sprite[][] sprites = RPGSprite.extractSprites("zelda/player",
                4, 1, 2,
                this, 16, 32, new Orientation[]{Orientation.DOWN,
                        Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
        Sprite[][] swordSprites= RPGSprite.extractSprites("zelda/player.sword",4,2,2,this,32,32,new Vector(-0.5f,0),new Orientation[]{Orientation.DOWN,
                Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        Sprite[][] bowSprites= RPGSprite.extractSprites("zelda/player.bow",4,2,2,this,32,32,new Vector(-0.5f,0),new Orientation[]{Orientation.DOWN,
                Orientation.UP, Orientation.RIGHT, Orientation.LEFT});

        Animation[] bowAnimation = RPGSprite.createAnimations(ANIMATION_DURATION/2, bowSprites,false);
        Animation[] swordAnimation = RPGSprite.createAnimations(ANIMATION_DURATION/2, swordSprites,false);
        Animation[] defaultAnimations = RPGSprite.createAnimations(ANIMATION_DURATION / 2, sprites);


        animations= new Animation[16];
        for(int i=0; i<16;i++){
            if(i<4) animations[i]=defaultAnimations[i];
            else if(i<8) animations[i] = swordAnimation[i-4];
            else if(i<12) animations[i] = bowAnimation[i-8];
        }

        inventory = new ARPGInventory(this, 100, 10, 1234);
        inventory.addItemToInventory(ARPGItem.BOMB, 3);
        inventory.addItemToInventory(ARPGItem.SWORD);
        inventory.addItemToInventory(ARPGItem.BOW);
        playerGUI = new ARPGPlayerStatusGUI( this, inventory.getCurrentItem().getSpriteName() );
    }

    public void update(float deltaTime) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Mouse mouse = getOwnerArea().getMouse();
        // mouseWheelInput can be either 0 (no movement) or 1 / -1 (movement)
        int mouseWheelInput = mouse.getMouseWheelInput();

        wantsInteraction = false;;

        // display animation if player is moving
        if( isDisplacementOccurs() || state!=PlayerStates.IDLE )
        {
            animations[currentAnimation].update(deltaTime);
            if(state!=PlayerStates.IDLE && animations[currentAnimation].isCompleted()){
                state= PlayerStates.IDLE;
                animations[currentAnimation].reset();
                setAnimationByOrientation(getOrientation());
            }
        }
        for ( PlayerInput input : PlayerInput.values() )
        {
            if ( keyboard.get( input.getKeyCode() ).isPressed() )
            {
                reactToInput( input );
            }
        }
        if ( mouseWheelInput != 0 )
        {
            takeNextItem( mouseWheelInput );
        }
        // register movement
        if(state==PlayerStates.IDLE){
            moveOrientate(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
            moveOrientate(Orientation.UP, keyboard.get(Keyboard.UP));
            moveOrientate(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
            moveOrientate(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
        }
        super.update(deltaTime);
    }

    private void reactToInput( PlayerInput input )
    {
        switch ( input ) {
            case INTERACT:
                wantsInteraction = true;
                break;
            case SHOW_INV:
                inventory.toggleDisplay();
                break;
            case USE_ITEM:
                useItem();
                break;
            case NEXT_ITEM:
                takeNextItem(1);
                break;
        }
    }

    private void useItem() {
        currentItem=getEquippedItem();
        if(currentItem==null) return;
        switch(currentItem){
            case BOMB:
                DiscreteCoordinates bombCoordinates = getFieldOfViewCells().get(0);
                if(isDisplacementOccurs()) bombCoordinates=bombCoordinates.jump(getOrientation().toVector());
                boolean registeredActor = getOwnerArea().registerActor(new Bomb(getOwnerArea(), Orientation.DOWN, bombCoordinates));
                if(registeredActor){
                    boolean removed = inventory.removeItemFromInventory(ARPGItem.BOMB);
                    if ( removed ) { playerGUI.setItemSprite( inventory.getCurrentItem().getSpriteName() ); }
                }
                break;
            case SWORD:
                if(state==state.IDLE){
                    wantsInteraction=true;
                    state=PlayerStates.ATTACKING_SWORD;
                    currentAnimation=currentAnimation+4;
                }
                break;
            case BOW:
                if(state==state.IDLE){
                    state=PlayerStates.ATTACKING_BOW;
                    currentAnimation=currentAnimation+8;
                }
        }
    }

    private void takeNextItem( int direction ) {
        currentItem = (ARPGItem) inventory.getNextItem( direction );
        playerGUI.setItemSprite( currentItem.getSpriteName() );
    }

    public ARPGItem getEquippedItem() {
        return (ARPGItem) inventory.getCurrentItem();
    }

    @Override
    public void draw(Canvas canvas) {
        playerGUI.draw( canvas );
        animations[currentAnimation].draw(canvas);
    }

    /**
     * Orientate or Move this player in the given orientation if the given button is down
     *
     * @param orientation (Orientation): given orientation, not null
     * @param btn         (Button): button corresponding to the given orientation, not null
     */
    protected void moveOrientate(Orientation orientation, Button btn) {
        if ( btn.isDown() )
        {
            if (getOrientation() == orientation) {
                move(ANIMATION_DURATION);
            } else{
                boolean orientationSuccessful = orientate(orientation);
                if (orientationSuccessful) {
                    setAnimationByOrientation(orientation);
                }

            }
        }
    }
    public void setAnimationByOrientation(Orientation orientation){
        switch (orientation) {
            case UP:
                currentAnimation = 0;
                break;
            case DOWN:
                currentAnimation = 2;
                break;
            case LEFT:
                currentAnimation = 3;
                break;
            case RIGHT:
                currentAnimation = 1;
                break;
        }
        animations[currentAnimation].reset();
    };
    public int getMoney() {
        return inventory.getMoney();
    }

    public int getMaxHP() {
        return maxHP;
    }

    public float getHp() {
        return hp;
    }

    public void giveDamage(float damage){
        hp-=damage;
        if(hp<0){
            hp=0;
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
        return true;
    }


    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this);
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

        public void interactWith( Coin coin )
        {
            inventory.addMoney( coin.getValue() );
            coin.collect();
        }

        public void interactWith( Heart heart )
        {
            hp += 1;
            if ( hp > maxHP )
            {
                hp = maxHP;
            }
            heart.collect();
        }
        @Override
        public void interactWith(CastleKey key){
            inventory.addItemToInventory(ARPGItem.CASTLE_KEY);
            key.collect();
        }
        @Override
        public void interactWith(CastleDoor door){
            door.passDoor();
        }

        @Override
        public void interactWith( Grass grass )
        {
            grass.cutGrass();
        }

        @Override
        public void interactWith(Monster monster)
        {
            monster.giveDamage( getEquippedItem().getDamage(), getEquippedItem().getVuln() );
        }
    }
}
