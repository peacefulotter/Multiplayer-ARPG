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

// All the variables in protected are used in NetworkARPGPlayer
public class ARPGPlayer extends Player {
    // Animation duration in frame number
    protected final static int ANIMATION_DURATION = 8;
    // protected, not final and not ARPGPlayerHandler type because it will be overwritten by NetworkARPGPlayer
    protected ARPGInteractionVisitor handler;
    protected boolean unReactive = false;
    protected ARPGInventory inventory;
    // different states of the player
    protected PlayerStates state;
    protected float hp;
    private static final int maxHP = 5;
    private Animation[][] animations;
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
    private Animation dashAnimation;

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
        setInitialInventory();
        playerGUI = new ARPGPlayerStatusGUI(this, inventory.getCurrentItem().getSpriteName());
    }

    /**
     *  Add some items to the player's inventory
     */
    private void setInitialInventory() {
        inventory.addItemToInventory(ARPGItem.BOMB, 10);
        inventory.addItemToInventory(ARPGItem.SWORD);
        inventory.addItemToInventory(ARPGItem.BOW);
        inventory.addItemToInventory(ARPGItem.STAFF);
        inventory.addItemToInventory(ARPGItem.ARROW, 10);
    }

    public void update(float deltaTime) {
        // while dashing the player cannot do anything else
        if (state == PlayerStates.IS_DASHING) {
            // if the dashAnimation is completed, then the dash is over, the player goes back to IDLE
            if (dashAnimation.isCompleted()) {
                state = PlayerStates.IDLE;
                dashAnimation.reset();
            // or the player moves straight-forward quickly
            } else {
                dashAnimation.update(deltaTime);
                move(5);
            }
        }
        // update animation if player is moving or has any state other than Idle
        else if (isDisplacementOccurs() || state != PlayerStates.IDLE) {
            animations[currentAnimation][currentAnimationDirection].update(deltaTime);
            // if the player is in any other state and its animation is completed
            if (state != PlayerStates.IDLE && animations[currentAnimation][currentAnimationDirection].isCompleted()) {
                // then it turns back to IDLE and the animation is reset
                state = PlayerStates.IDLE;
                animations[currentAnimation][currentAnimationDirection].reset();
                setAnimationByOrientation(getOrientation());
            }
        }

        // necessary for NetworkARPGPlayer, player is unReactive when it is not the Client's player
        if (unReactive) {
            super.update(deltaTime);
            return;
        }

        // get the keyboard and mouse input
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Mouse mouse = getOwnerArea().getMouse();
        // mouseWheelInput can be either 0 (no movement) or 1 / -1 (movement)
        int mouseWheelInput = mouse.getMouseWheelInput();

        wantsViewInteraction = false;

        // for every player input check if the corresponding keycaps is pressed and if so react to the input
        for (PlayerInput input : PlayerInput.values()) {
            boolean reactToInput = false;
            if (input.getCanHoldDown() && keyboard.get(input.getKeyCode()).isDown()) { reactToInput = true; }
            if (keyboard.get(input.getKeyCode()).isPressed()) { reactToInput = true; }
            if (reactToInput) { reactToInput(input); }
        }

        // of there is an input from the mouse wheel, then takes the next item
        // if mouseWheelInput is -1 : Take the previous item
        // if it is 1 : Take the next one
        if (mouseWheelInput != 0) {
            takeNextItem(mouseWheelInput);
        }
        // register movement if the player is IDLE
        if (state == PlayerStates.IDLE) {
            // move the player according to the movement inputs (witht the arrow)
            moveOrientate(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
            moveOrientate(Orientation.UP, keyboard.get(Keyboard.UP));
            moveOrientate(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
            moveOrientate(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
            currentAnimation = 0;
        }
        super.update(deltaTime);
    }

    /**
     * React to input depending on the Input type
     * @param input
     */
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

    /**
     * Make the player dash
     */
    private void playerDash() {
        // only allow the player to dash if he is moving and is IDLE
        if (isDisplacementOccurs() && state == PlayerStates.IDLE) {
            state = PlayerStates.IS_DASHING;
            dashStartingPos = getCurrentCells().get(0).toVector();
        }
    }

    /**
     * Use the item the player is currently equipped with
     */
    protected void useItem() {
        currentItem = getEquippedItem();
        // use the item only if the player is IDLE
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
                // try to register the bomb
                boolean registeredActor = getOwnerArea().registerActor(new Bomb(getOwnerArea(), Orientation.DOWN, bombCoordinates));
                // if it successfully registered, then remove one bomb from his inventory
                if (registeredActor) {
                    inventory.removeItemFromInventory(ARPGItem.BOMB);
                }
                break;
            case SWORD:
                // if he uses his sword, then he wants viewInteraction
                wantsViewInteraction = true;
                state = PlayerStates.ATTACKING_SWORD;
                currentAnimation = 1;
                break;
            case BOW:
                // if the player has at least one arrow in his inventory
                if ( inventory.removeItemFromInventory((InventoryItem) ARPGItem.ARROW) ) {
                    state = PlayerStates.ATTACKING_BOW;
                    // throw an arrow in front of him
                    getOwnerArea().registerActor(new Arrow(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 2, 5));
                    currentAnimation = 2;
                }
                break;
            case STAFF:
                // create a Magic Projectile in front of him
                state = PlayerStates.ATTACKING_STAFF;
                getOwnerArea().registerActor(new MagicProjectile(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 2, 5));
                currentAnimation = 3;
                break;
        }
    }

    /**
     * get the next / previous item in his inventory depending on the direction
     * @param direction
     */
    private void takeNextItem(int direction) {
        currentItem = (ARPGItem) inventory.getNextItem(direction);
        // and update the GUI to match the new item
        playerGUI.setItemSprite(currentItem.getSpriteName());
    }

    /**
     * Get the item the player is equipped with
     * @return ARPGItem : the item he is equipped with
     */
    public ARPGItem getEquippedItem() {
        return (ARPGItem) inventory.getCurrentItem();
    }


    @Override
    public void draw(Canvas canvas) {
        if (!unReactive) { playerGUI.draw(canvas); }
        // if the player is dashing, draw the dash animation at the position when he started to dash
        if (state == PlayerStates.IS_DASHING) {
            dashAnimation.setAnchor(dashStartingPos.sub(getCurrentCells().get(0).toVector()));
            dashAnimation.draw(canvas);
        }
        // and finally draw the player animation depending on his state (currentAnimation)
        animations[currentAnimation][currentAnimationDirection].draw(canvas);
    }

    /**
     * Orientate or Move this player in the given orientation if the given button is down
     *
     * @param orientation (Orientation): given orientation, not null
     * @param btn         (Button): button corresponding to the given orientation, not null
     */
    protected void moveOrientate(Orientation orientation, Button btn) {
        if (btn.isDown()) {
            moveOrientate(orientation);
        }
    }

    /**
     * move the player or change his orientation
     * @param orientation : player orientation
     */
    protected void moveOrientate(Orientation orientation) {
        if (getOrientation() == orientation) {
            move(ANIMATION_DURATION);
        } else {
            boolean orientationSuccessful = orientate(orientation);
            if (orientationSuccessful) {
                setAnimationByOrientation(orientation);
            }

        }
    }

    /**
     * Set the animation index depending on the player orientation
     * @param orientation : player orientation
     */
    public void setAnimationByOrientation(Orientation orientation) {
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
    public Animation[] getBowAnimation()
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
            // deal damage to the monsters only if the vulnerabilities of the equipped item match
            // one of the vulnerabilities of the monster
            if (monster.getVulnerabilities().contains(getEquippedItem().getVuln())) {
                monster.giveDamage(getEquippedItem().getDamage());
            }
        }
    }
}
