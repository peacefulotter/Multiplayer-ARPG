package ch.epfl.cs107.play.game.narpg.actor.player;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.MovableNetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.Networking.Packets.Packet02Move;
import ch.epfl.cs107.play.Networking.Packets.Packet03Update;
import ch.epfl.cs107.play.Networking.Packets.Packet04Chat;
import ch.epfl.cs107.play.Networking.utils.IdGenerator;
import ch.epfl.cs107.play.Networking.utils.OrientationValues;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.arpg.actor.monster.Vulnerabilities;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.player.PlayerStates;
import ch.epfl.cs107.play.game.narpg.actor.NetworkBomb;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.actor.projectiles.NetworkArrow;
import ch.epfl.cs107.play.game.narpg.actor.projectiles.NetworkMagic;
import ch.epfl.cs107.play.game.narpg.areas.NetworkArena;
import ch.epfl.cs107.play.game.narpg.handler.NARPGInteractionVisitor;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkCoin;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkHeart;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.TextAlign;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkARPGPlayer extends ARPGPlayer implements MovableNetworkEntity {
    private static final float DEPTH = 10000;
    private static final float HEART_SIZE = .7f;
    private static final float MAX_ARROW_RANGE = 10;
    private static final float MAX_ARROW_DAMAGE = 2;
    private static final int MIN_ARROW_SPEED = 1;
    private static final int MIN_BOW_DURATION = 1;
    private final boolean clientAuthority;
    private final long connectionId;
    private boolean dead;
    private Connection connection;
    private Area currentArea;
    private int id;
    private TextGraphics usernameText;
    //add updates to queue so they can be sent at the same time
    private HashMap<String, String> queuedUpdates;
    //to check if the packet that sets the correct position after movement has been sent
    private boolean hasSentCorrectPosition = true;
    private int arrowSpeed;
    private float arrowRange;
    private float arrowDamage;
    private int bowAnimationDuration;
    private int killer;
    private int playerKills;
    private boolean showUpgrades;
    /**
     * Default Player constructor
     *
     * @param area        (Area): Owner Area, not null
     * @param orientation (Orientation): Initial player orientation, not null
     * @param coordinates (Coordinates): Initial position, not null
     */
    public NetworkARPGPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates, Connection connection, boolean clientAuthority, long connectionId, String username, int id) {
        super(area, orientation, coordinates);
        this.handler = new NetworkARPGPlayerHandler();
        this.queuedUpdates = new HashMap<String, String>();
        this.currentArea = area;
        this.connection = connection;
        this.connectionId = connectionId;
        // id of 0 is used as null value for id
        if (id == 0) {
            this.id = IdGenerator.generateId();
        }
        this.clientAuthority = clientAuthority;
        this.state = PlayerStates.IDLE;
        if (!clientAuthority) {
            unReactive = true;
        }
        if (username == null) {
            username = "";
        }
        usernameText = new TextGraphics(username, .5f, Color.WHITE, Color.BLACK, .005f, true, false, new Vector(+.4f, +1.5f), TextAlign.Horizontal.CENTER, null, 1f, 10000);
        usernameText.setParent(this);
        arrowRange = 3;
        arrowSpeed = 6;
        arrowDamage = 1;
        bowAnimationDuration = ANIMATION_DURATION;
        playerKills = 0;
        showUpgrades = false;
        playerGUI =  new NetworkARPGPlayerGUI( this, getEquippedItem().getSpriteName() );
    }

    public NetworkARPGPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates, Connection connection, boolean clientAuthority, HashMap<String, String> initialState) {
        this(area, orientation, coordinates, connection, clientAuthority, Long.parseLong(initialState.get("connectionId")), initialState.get("username"), Integer.parseInt(initialState.get("id")));
        updateState(initialState);
    }

    public boolean isShowUpgrades() { return showUpgrades; }

    public boolean isDead() {
        return dead;
    }

    public int getKiller() {
        return killer;
    }

    public long getConnectionId() {
        return connectionId;
    }

    @Override
    public void update(float deltaTime) {
        if (!queuedUpdates.isEmpty()) {
            Packet03Update updatePacket = new Packet03Update(id, queuedUpdates);
            updatePacket.writeData(connection);
            queuedUpdates = new HashMap<String, String>();
        }

        if (!connection.isServer() && clientAuthority) {
            Keyboard keyboard = getOwnerArea().getKeyboard();
            Orientation moved = null;
            if (connection != null) {
                if (keyboard.get(keyboard.LEFT).isDown()) {
                    moved = Orientation.LEFT;
                } else if (keyboard.get(keyboard.UP).isDown()) {
                    moved = Orientation.UP;
                } else if (keyboard.get(keyboard.RIGHT).isDown()) {
                    moved = Orientation.RIGHT;
                } else if (keyboard.get(keyboard.DOWN).isDown()) {
                    moved = Orientation.DOWN;
                } else if (keyboard.get(keyboard.SPACE).isPressed()) {
                    useItem();
                } else if (keyboard.get(keyboard.Y).isPressed()) {
                    new Packet04Chat("j'aime ce jeu !").writeData(connection);
                }
                if ( showUpgrades )
                {
                    if ( keyboard.get( Keyboard.U ).isPressed() )
                    {
                        arrowRange = increaseArrowStat( arrowRange, 1, MAX_ARROW_RANGE, "range" );
                    } else if ( keyboard.get( Keyboard.I ).isPressed() )
                    {
                        reduceBowAnimationDuration();
                    }
                    else if ( keyboard.get( Keyboard.O ).isPressed() )
                    {
                        arrowDamage = increaseArrowStat( arrowDamage, 0.5f, MAX_ARROW_DAMAGE, "damage" );
                    }
                    else if ( keyboard.get( Keyboard.P ).isPressed() ) {
                        arrowSpeed = increaseArrowSpeed();
                    }
                }
                super.update(deltaTime);
                if (moved != null && isDisplacementOccurs()) {
                    hasSentCorrectPosition = false;
                    Packet02Move packet = new Packet02Move(id, moved, getCurrentMainCellCoordinates(), ANIMATION_DURATION);
                    System.out.println("data : "+ new String(packet.getData()));
                    packet.writeData(connection);
                }
                if (!isDisplacementOccurs() && !hasSentCorrectPosition) {
                    DiscreteCoordinates currentDisPos = getCurrentMainCellCoordinates();
                    queuedUpdates.put("position", currentDisPos.x + "," + currentDisPos.y);
                    queuedUpdates.put("orientation", String.valueOf(OrientationValues.getOrientationValue(getOrientation())));
                    hasSentCorrectPosition = true;
                }
                return;
            }


        }
        super.update(deltaTime);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        usernameText.draw(canvas);
        if (clientAuthority) {
            return;
        }

        float hp = getHp();
        int hearts = getMaxHP();
        ImageGraphics[] heartsDisplay = new ImageGraphics[hearts];

        for (int i = 0; i < hearts; i++) {
            int spriteOffset = 0;
            if (hp >= 1) {
                spriteOffset = 32;
                hp--;
            } else if (hp == 0.5) {
                spriteOffset = 16;
                hp -= 0.5f;
            }
            heartsDisplay[i] = new ImageGraphics(
                    ResourcePath.getSprite("zelda/heartDisplay"),
                    HEART_SIZE, HEART_SIZE, new RegionOfInterest(spriteOffset, 0, 16, 16),
                    this.getPosition(), 1, DEPTH);
            heartsDisplay[i].setAnchor(
                    this.getPosition().add(
                            (HEART_SIZE + .1f) * i + .5f - 0.5f * (getMaxHP() * (HEART_SIZE + .1f)), -HEART_SIZE));
            heartsDisplay[i].draw(canvas);

        }
    }

    @Override
    public void updateState(HashMap<String, String> updateMap) {
        for (Map.Entry<String, String> entry : updateMap.entrySet()) {
            switch (entry.getKey()) {
                case "username":
                    usernameText.setText(entry.getValue());
                    break;
                case "hp":
                    hp = Float.parseFloat(entry.getValue());
                    if(hp<1){
                        dead=true;
                    }
                    break;
                case "position":
                    if (clientAuthority || isDisplacementOccurs()) return;
                    String[] pos = entry.getValue().split(",");
                    DiscreteCoordinates position = new DiscreteCoordinates(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
                    if (!getCurrentMainCellCoordinates().equals(position)) {
                        //System.out.println("UPDATING POS : " + position.toVector() + " ; " + getCurrentMainCellCoordinates());
                        ((NetworkArena) getOwnerArea()).getBehavior().leave(this, getCurrentCells());
                        getOwnerArea().enterAreaCells(this, Collections.singletonList(position));
                        setCurrentPosition(position.toVector());
                    }
                    break;
                case "orientation":
                    if (clientAuthority || isDisplacementOccurs()) return;
                    orientate(OrientationValues.getOrientationByValue(Integer.parseInt(entry.getValue())));

            }
        }
    }

    @Override
    protected void useItem() {
        if (state != PlayerStates.IDLE) return;
        switch (getEquippedItem()) {
            case BOMB:
                new NetworkBomb(getOwnerArea(), Orientation.DOWN, inFronOfPlayer(), id).getSpawnPacket().writeData(connection);

                break;
            case SWORD:
                super.useItem();
                break;
            case BOW:
                System.out.println( arrowSpeed + " " + arrowRange + " " + arrowDamage );
                setState(PlayerStates.ATTACKING_BOW);
                new NetworkArrow(getOwnerArea(), getOrientation()
                        , inFronOfPlayer(),
                        connection, arrowSpeed, arrowRange, arrowDamage, id, 0).getSpawnPacket().writeData(connection);
                currentAnimation = 2;
                break;
            case STAFF:
                setState(PlayerStates.ATTACKING_STAFF);
                new NetworkMagic(getOwnerArea(), getOrientation(), inFronOfPlayer(), connection, 10, 10, id).getSpawnPacket().writeData(connection);
                currentAnimation = 3;
                break;
        }
    }

    private DiscreteCoordinates inFronOfPlayer() {
        return getCurrentMainCellCoordinates().jump(getOrientation().toVector());
    }

    @Override
    public void giveDamage( float damage )
    {
        super.giveDamage( damage );
        queuedUpdates.put( "hp",String.valueOf( getHp() ) );
    }

    public void giveDamage(float damage, int givenBy)
    {
        giveDamage( damage );
        if ( hp < 1 )
        {
            killer = givenBy;
            dead = true;
        }
    }

    private void privateMessage( String text )
    {
        ((NetworkArena)getOwnerArea()).getAnnouncement().addAnnouncement( text );
    }

    private float increaseArrowStat( float stat, float increase, float bound, String name )
    {
        stat += increase;
        if ( stat > bound )
        {
            stat = bound;
            privateMessage( name + " already fully upgraded" );
        } else
        {
            privateMessage( "Upgraded arrow" + name + " to " + stat );
            //showUpgrades = false;
        }
        return stat;
    }

    private int increaseArrowSpeed() {
        arrowSpeed--;
        if ( arrowSpeed <= MIN_ARROW_SPEED )
        {
            arrowSpeed = MIN_ARROW_SPEED;
            privateMessage( "Arrow speed already fully upgraded" );
        } else
        {
            privateMessage( "Upgraded Arrow Speed to " + arrowSpeed );
            //showUpgrades = false;
        }
        return arrowSpeed;
    }

    private void reduceBowAnimationDuration()
    {
        if ( bowAnimationDuration >= MIN_BOW_DURATION + 1 )
        {
            for ( Animation animation : getBowAnimation() )
            {
                animation.setSpeedFactor(--bowAnimationDuration);
            }
            privateMessage( "Upgraded Bow Reload Speed");
            //showUpgrades = false;
        } else {
            privateMessage( "Bow already fully upgraded");
        }

    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setId(int objectId) {
        this.id = objectId;
    }

    public String getUsername() {
        return usernameText.getText();
    }

    public void setState(PlayerStates state) {
        this.state = state;
    }


    @Override
    public Packet00Spawn getSpawnPacket() {
        HashMap initalState = new HashMap();
        initalState.put("username", usernameText.getText());
        initalState.put("id", String.valueOf(id));
        initalState.put("hp", String.valueOf(hp));
        initalState.put("connectionId", String.valueOf(connectionId));
        return new Packet00Spawn(getId(), NetworkEntities.PLAYER, getOrientation(), getCurrentCells().get(0), initalState);
    }

    @Override
    public void networkMove(Packet02Move movePacket) {
        Orientation orientation = movePacket.getOrientation();
        int speed = movePacket.getSpeed();
        List<DiscreteCoordinates> positionBeforeMoving = getCurrentCells();
        DiscreteCoordinates startPosition = movePacket.getStart();
        if (!isDisplacementOccurs() || isTargetReached()) {
            super.orientate(orientation);
            if (!getCurrentCells().get(0).equals(startPosition)) {
                ((NetworkArena) getOwnerArea()).getBehavior().leave(this, positionBeforeMoving);
                ((NetworkArena) getOwnerArea()).getBehavior().leave(this, Collections.singletonList(positionBeforeMoving.get(0).jump(getOrientation().toVector())));
                setCurrentPosition(startPosition.toVector());
                //Very useful for debugging
                //System.out.println("Setting position  : " + positionBeforeMoving+ " ; " +startPosition);
            }
            setAnimationByOrientation(orientation);
            move(speed);
        }
    }

    public boolean isClientAuthority() {
        return clientAuthority;
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        if (connection.isServer()) {
            ((NARPGInteractionVisitor) v).interactWith(this);
        }
    }

    @Override
    public void interactWith(Interactable other) {
        if (connection.isServer()) {
            other.acceptInteraction(handler);
        }
    }

    class NetworkARPGPlayerHandler implements NARPGInteractionVisitor {

        @Override
        public void interactWith(NetworkARPGPlayer player) {
            if (state != PlayerStates.IDLE && getEquippedItem().getVuln() == Vulnerabilities.CLOSE_RANGE) {
                player.giveDamage(getEquippedItem().getDamage());
                System.out.println(player.isDead());
                if ( player.isDead() )
                {
                    playerKills++;
                    showUpgrades = true;
                }
            }
        }

        @Override
        public void interactWith(NetworkCoin coin) {
            coin.collect();
            queuedUpdates.put("money", String.valueOf(inventory.getMoney() + 50));
        }

        public void interactWith(NetworkHeart heart) {
            hp++;
            if (hp > getMaxHP()) {
                hp = getMaxHP();
            }
            queuedUpdates.put("hp", String.valueOf(hp));
        }
    }
}
