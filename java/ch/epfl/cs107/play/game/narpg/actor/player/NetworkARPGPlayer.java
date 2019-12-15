package ch.epfl.cs107.play.game.narpg.actor.player;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.MovableNetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.Networking.Packets.Packet02Move;
import ch.epfl.cs107.play.Networking.Packets.Packet03Update;
import ch.epfl.cs107.play.Networking.utils.IdGenerator;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.monster.Vulnerabilities;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.player.PlayerStates;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.handler.NARPGInteractionVisitor;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkCoin;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkHeart;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.HashMap;

public class NetworkARPGPlayer extends ARPGPlayer implements MovableNetworkEntity {
    private Connection connection;
    private Area currentArea;
    private int id;
    private boolean clientAuthority;
    private String playerMoney;
    private PlayerStates state;
    private final NetworkARPGPlayerHandler handler;

    /**
     * Default Player constructor
     *
     * @param area        (Area): Owner Area, not null
     * @param orientation (Orientation): Initial player orientation, not null
     * @param coordinates (Coordinates): Initial position, not null
     */
    public NetworkARPGPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates, Connection connection, boolean clientAuthority) {
        super(area, orientation, coordinates);
        this.handler = new NetworkARPGPlayerHandler();
        this.currentArea = area;
        this.connection = connection;
        this.id = IdGenerator.generateId();
        this.clientAuthority = clientAuthority;
        this.state = PlayerStates.IDLE;
        if (!clientAuthority) unReactive = true;
    }

    @Override
    public void update(float deltaTime) {
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
                } else if ( keyboard.get( keyboard.SPACE ).isPressed() ) {
                    useItem();
                }
                if (moved != null) {
                    Packet02Move packet = new Packet02Move(id, moved, getCurrentMainCellCoordinates(), ANIMATION_DURATION);
                    packet.writeData(connection);
                }
            }

        }
        super.update(deltaTime);
    }

    protected void useItem() {
        switch ( getEquippedItem() ) {
            case BOMB:
                new Packet00Spawn(
                        NetworkEntities.BOMB.getClassId(), NetworkEntities.BOMB, Orientation.DOWN, getNextCurrentCells().get(0), currentArea
                ).writeData( connection );
                break;
            case SWORD:
                super.useItem();
                System.out.println(state);
                break;
            case BOW:
                new Packet00Spawn(
                        NetworkEntities.BOW.getClassId(), NetworkEntities.BOW, getOrientation(), getNextCurrentCells().get(0), currentArea
                ).writeData( connection );
                break;
            case STAFF:
                new Packet00Spawn(
                        NetworkEntities.STAFF.getClassId(), NetworkEntities.STAFF, getOrientation(), getNextCurrentCells().get(0), currentArea
                ).writeData( connection );
                break;
        }
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setId(int objectId) {
        this.id = objectId;
    }

    public void setState(PlayerStates state)
    {
        this.state = state;
    }

    @Override
    public void setPosition(DiscreteCoordinates position) {
    }

    @Override
    public void setOrientation(Orientation orientation) {
        orientate(orientation);
    }

    @Override
    public Packet00Spawn getSpawnPacket() {
        return new Packet00Spawn(getId(), NetworkEntities.PLAYER, getOrientation(), getCurrentCells().get(0), currentArea);
    }

    @Override
    public void networkMove(Packet02Move movePacket) {
        Orientation orientation = movePacket.getOrientation();
        int speed = movePacket.getSpeed();
        DiscreteCoordinates startPosition = movePacket.getStart();
        if (!isDisplacementOccurs() || isTargetReached()) {
            super.orientate(orientation);
            getOwnerArea().leaveAreaCells(this, getCurrentCells());
            getOwnerArea().enterAreaCells(this, getCurrentCells());
            setCurrentPosition(startPosition.toVector());
            setAnimationByOrientation(orientation);
            move(speed);
        }

    }

    public boolean isClientAuthority() {
        return clientAuthority;
    }

    public void setPlayerMoney(String playerMoney) {
        this.playerMoney = playerMoney;
        inventory.addMoney(Integer.parseInt(playerMoney)-inventory.getMoney());
    }

    class NetworkARPGPlayerHandler implements NARPGInteractionVisitor
    {

        @Override
        public void interactWith( NetworkARPGPlayer player )
        {
            if ( getEquippedItem().getVuln() == Vulnerabilities.CLOSE_RANGE )
            {
                player.giveDamage( getEquippedItem().getDamage() );
            }
        }

        @Override
        public void interactWith( NetworkCoin coin ) {
            coin.collect();
            HashMap<String, String> changeMap = new HashMap();
            changeMap.put("playerMoney", String.valueOf(getMoney() + 50));
            var updatePacket = new Packet03Update(getId(), changeMap);
            updatePacket.writeData(connection);
        }

        public void interactWith( NetworkHeart heart )
        {
            heart.collect();
        }
    }
}
