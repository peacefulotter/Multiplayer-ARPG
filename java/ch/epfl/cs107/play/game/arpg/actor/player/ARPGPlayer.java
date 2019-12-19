package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.CastleDoor;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.monster.Monster;
import ch.epfl.cs107.play.game.arpg.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.arpg.actor.projectiles.MagicProjectile;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGInventory;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGItem;
import ch.epfl.cs107.play.game.arpg.inventory.items.CastleKey;
import ch.epfl.cs107.play.game.arpg.inventory.items.Coin;
import ch.epfl.cs107.play.game.arpg.inventory.items.Heart;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.game.rpg.inventory.InventoryItem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Mouse;

import java.util.Collections;
import java.util.List;

public class ARPGPlayer extends Player {
    /// Animation duration in frame number
    protected final static int ANIMATION_DURATION = 8;
    // protected, not final and not ARPGPlayerHandler type because it will be overwritten by NetworkARPGPlayer
    protected ARPGInteractionVisitor handler;
    protected boolean unReactive = false;
    protected ARPGInventory inventory;
    protected PlayerStates state;
    protected float hp;
    private static final int maxHP = 5;
    private final Animation[][] animations;
    //Animation index that chooses between running, sword, bow or staff animation
    protected int currentAnimation = 0;
    //Animation index that chooses the correct orientation of currentAnimation
    private int currentAnimationDirection = 2;
    //wanteViewInteraction can change based on if the player is attacking with sword or not
    private boolean wantsViewInteraction = false;
    //Item currently equipped
    private ARPGItem currentItem;
    // not private, and not final because it will be overwritten by NetworkARPGPlayer
    protected ARPGPlayerStatusGUI playerGUI;
    private Vector dashStartingPos;
    private final Animation dashAnimation;

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

        Sprite[][] sprites = RPGSprite.extractSprites("zelda/player", 4, 1, 2, this, 16, 32, new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
        Sprite[][] swordSprites = RPGSprite.extractSprites("zelda/player.sword", 4, 2, 2, this, 32, 32, new Vector(-0.5f, 0), new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        Sprite[][] bowSprites = RPGSprite.extractSprites("zelda/player.bow", 4, 2, 2, this, 32, 32, new Vector(-0.5f, 0), new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        Sprite[][] staffSprites = RPGSprite.extractSprites("zelda/player.staff_water", 4, 2, 2, this, 32, 32, new Vector(-0.5f, 0), new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        //Custom dash sprites;
        Sprite[] dashAnimationSprites = new Sprite[5];
        for (int i = 2; i < 7; i++) {
            dashAnimationSprites[i - 2] = new Sprite("zelda/vanish", 1f, 1f, this, new RegionOfInterest(i * 32, 0, 32, 32), Vector.ZERO, 1f, -100);
        }
        dashAnimation = new Animation(5, dashAnimationSprites, false);

        //creates running, sword swinging, bow and staff shooting animations
        animations = new Animation[][]{
                RPGSprite.createAnimations(ANIMATION_DURATION / 2, sprites, true),
                RPGSprite.createAnimations(ANIMATION_DURATION / 2, swordSprites, false),
                RPGSprite.createAnimations(ANIMATION_DURATION / 2, bowSprites, false),
                RPGSprite.createAnimations(ANIMATION_DURATION / 2, staffSprites, false)
        };

        inventory = new ARPGInventory( 100, 10, 1234);
        inventory= setInitialInventory(inventory);
        playerGUI = new ARPGPlayerStatusGUI(this, inventory.getCurrentItem().getSpriteName());
    }

    private ARPGInventory setInitialInventory(ARPGInventory inventory){
        inventory.addItemToInventory(ARPGItem.BOMB, 10);
        inventory.addItemToInventory(ARPGItem.SWORD);
        inventory.addItemToInventory(ARPGItem.BOW);
        inventory.addItemToInventory(ARPGItem.STAFF);
        inventory.addItemToInventory(ARPGItem.ARROW, 10);
        return inventory;
    }
    public void update(float deltaTime) {
        if (state == PlayerStates.IS_DASHING) {
            if (dashAnimation.isCompleted()) {
                state = PlayerStates.IDLE;
                dashAnimation.reset();
            } else {
                dashAnimation.update(deltaTime);
                move(5);
            }
        }
        // display animation if player is moving or has any state other than Idle
        else if (isDisplacementOccurs() || state != PlayerStates.IDLE) {
            animations[currentAnimation][currentAnimationDirection].update(deltaTime);
            if (state != PlayerStates.IDLE && animations[currentAnimation][currentAnimationDirection].isCompleted()) {
                state = PlayerStates.IDLE;
                animations[currentAnimation][currentAnimationDirection].reset();
                setAnimationByOrientation(getOrientation());
            }
        }
        if (unReactive) {
            super.update(deltaTime);
            return;
        }
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Mouse mouse = getOwnerArea().getMouse();
        // mouseWheelInput can be either 0 (no movement) or 1 / -1 (movement)
        int mouseWheelInput = mouse.getMouseWheelInput();

        wantsViewInteraction = false;


        for (PlayerInput input : PlayerInput.values()) {
            boolean reactToInput = false;
            if (input.getCanHoldDown() && keyboard.get(input.getKeyCode()).isDown()) reactToInput = true;
            if (keyboard.get(input.getKeyCode()).isPressed()) reactToInput = true;
            if (reactToInput) reactToInput(input);
        }

        if (mouseWheelInput != 0) {
            takeNextItem(mouseWheelInput);
        }
        // register movement
        if (state == PlayerStates.IDLE) {
            moveOrientate(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
            moveOrientate(Orientation.UP, keyboard.get(Keyboard.UP));
            moveOrientate(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
            moveOrientate(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
            currentAnimation = 0;
        }
        super.update(deltaTime);
    }

    private void reactToInput(PlayerInput input) {
        switch (input) {
            case INTERACT:
                wantsViewInteraction = true;
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
            case DASH:
                playerDash();
                break;
        }
    }

    private void playerDash() {
        if (isDisplacementOccurs() && state == PlayerStates.IDLE) {
            state = PlayerStates.IS_DASHING;
            dashStartingPos = getCurrentCells().get(0).toVector();
        }
    }

    protected void useItem() {
        currentItem = getEquippedItem();
        if (state != state.IDLE || currentItem == null ) { return; }
        switch (currentItem) {
            case BOMB:
                //Makes sure bomb is only placed if the use item key was just pressed and not when just  held down
                if(!getOwnerArea().getKeyboard().get(PlayerInput.USE_ITEM.getKeyCode()).isPressed()) return;
                //handles adding a bomb to the area and removing it from inventory
                DiscreteCoordinates bombCoordinates = getFieldOfViewCells().get(0);
                if ( isDisplacementOccurs() )
                {
                    bombCoordinates = bombCoordinates.jump( getOrientation().toVector() );
                }
                boolean registeredActor = getOwnerArea().registerActor(new Bomb(getOwnerArea(), Orientation.DOWN, bombCoordinates));
                if (registeredActor) {
                    inventory.removeItemFromInventory(ARPGItem.BOMB);

                }
                break;
            case SWORD:
                wantsViewInteraction = true;
                state = PlayerStates.ATTACKING_SWORD;
                currentAnimation = 1;
                break;
            case BOW:
                if ( inventory.removeItemFromInventory((InventoryItem) ARPGItem.ARROW) ) {
                    state = PlayerStates.ATTACKING_BOW;
                    getOwnerArea().registerActor(new Arrow(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 2, 5));
                    currentAnimation = 2;
                }
                break;
            case STAFF:
                state = PlayerStates.ATTACKING_STAFF;
                getOwnerArea().registerActor(new MagicProjectile(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 2, 5));
                currentAnimation = 3;
                break;
        }
    }

    private void takeNextItem(int direction) {
        currentItem = (ARPGItem) inventory.getNextItem(direction);
        playerGUI.setItemSprite(currentItem.getSpriteName());
    }

    public ARPGItem getEquippedItem() {
        return (ARPGItem) inventory.getCurrentItem();
    }

    @Override
    public void draw(Canvas canvas) {
        if(!unReactive)playerGUI.draw(canvas);
        if (state == PlayerStates.IS_DASHING) {
            dashAnimation.setAnchor(dashStartingPos.sub(getCurrentCells().get(0).toVector()));
            dashAnimation.draw(canvas);
        }
        animations[currentAnimation][currentAnimationDirection].draw(canvas);
    }

    /**
     * Orientate or Move this player in the given orientation if the given button is down
     *
     * @param orientation (Orientation): given orientation, not null
     * @param btn         (Button): button corresponding to the given orientation, not null
     */
    private void moveOrientate(Orientation orientation, Button btn) {
        if (btn.isDown()) {
            moveOrientate(orientation);
        }
    }

    private void moveOrientate(Orientation orientation) {
        if (getOrientation() == orientation) {
            move(ANIMATION_DURATION);
        } else {
            boolean orientationSuccessful = orientate(orientation);
            if (orientationSuccessful) {
                setAnimationByOrientation(orientation);
            }

        }
    }

    protected void setAnimationByOrientation(Orientation orientation) {
        switch (orientation) {
            case UP:
                currentAnimationDirection = 0;
                break;
            case DOWN:
                currentAnimationDirection = 2;
                break;
            case LEFT:
                currentAnimationDirection = 3;
                break;
            case RIGHT:
                currentAnimationDirection = 1;
                break;
        }
        animations[currentAnimation][currentAnimationDirection].reset();
    }


    // used in NetworkARPGPlayer to modify the bow animation duration
    protected Animation[] getBowAnimation()
    {
        return animations[ 2 ];
    }

    public int getMoney() {
        return inventory.getMoney();
    }

    public int getMaxHP() {
        return maxHP;
    }

    public float getHp() {
        return hp;
    }

    public void giveDamage(float damage) {
        hp -= damage;
        if (hp < 0) {
            hp = 0;
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
        return wantsViewInteraction;
    }


    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }


    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor) v).interactWith(this);
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    protected class ARPGPlayerHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(Door door) {
            if (door.isOpen()) {
                setIsPassingADoor(door);
            }
        }

        @Override
        public void interactWith(Coin coin) {
            inventory.addMoney(coin.getValue());
            coin.collect();
        }

        @Override
        public void interactWith(Heart heart) {
            hp += 1;
            if (hp > maxHP) {
                hp = maxHP;
            }
            heart.collect();
        }

        @Override
        public void interactWith(CastleKey key) {
            inventory.addItemToInventory(ARPGItem.CASTLE_KEY);
            key.collect();
        }

        @Override
        public void interactWith(CastleDoor door) {
            if (!door.isOpen() && inventory.getCurrentItem() == ARPGItem.CASTLE_KEY) {
                door.openDoor();
            } else if (door.isOpen()) {
                door.passDoor();
                setIsPassingADoor(door);
            } else {
                System.out.println("You need the key");
            }
        }

        @Override
        public void interactWith(Grass grass) {
            if (state.isCloseRangeAttacking()) {
                grass.cutGrass();
            }
        }

        @Override
        public void interactWith(Monster monster) {
            if (monster.getVulnerabilities().contains(getEquippedItem().getVuln())) {
                monster.giveDamage(getEquippedItem().getDamage());
            }
        }
    }
}
