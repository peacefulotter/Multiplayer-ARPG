package ch.epfl.cs107.play.game.narpg.actor.player;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.MovableNetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.Networking.Packets.Packet02Move;
import ch.epfl.cs107.play.Networking.Packets.Packet03Update;
import ch.epfl.cs107.play.Networking.utils.IdGenerator;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.monster.Vulnerabilities;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.player.PlayerStates;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.handler.NARPGInteractionVisitor;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkCoin;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkHeart;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.TextAlign;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NetworkARPGPlayer extends ARPGPlayer implements MovableNetworkEntity {
    private Connection connection;
    private Area currentArea;
    private int id;
    private boolean clientAuthority;
    private String playerMoney;
    private PlayerStates state;
    private TextGraphics usernameText;

    /**
     * Default Player constructor
     *
     * @param area        (Area): Owner Area, not null
     * @param orientation (Orientation): Initial player orientation, not null
     * @param coordinates (Coordinates): Initial position, not null
     */
    public NetworkARPGPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates, Connection connection, boolean clientAuthority, String username) {
        super(area, orientation, coordinates);
        handler = new NetworkARPGPlayerHandler();
        this.currentArea = area;
        this.connection = connection;
        this.id = IdGenerator.generateId();
        this.clientAuthority = clientAuthority;
        this.state = PlayerStates.IDLE;
        if (!clientAuthority) unReactive = true;

        usernameText = new TextGraphics(username,.5f,Color.WHITE,Color.BLACK,.005f,true,false,new Vector(+.4f,+1.5f), TextAlign.Horizontal.CENTER,null,1f,10000);
        usernameText.setParent(this);
    }
    public NetworkARPGPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates, Connection connection, boolean clientAuthority) {
        this(area,orientation,coordinates,connection,clientAuthority,"");
    }

    @Override
    public void update(float deltaTime)
    {
        var updateMap = new HashMap<String,String>();
        updateMap.put( "username", usernameText.getText() );
        new Packet03Update( getId(), updateMap );

        if ( !connection.isServer() && clientAuthority )
        {
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


    @Override
    public void draw( Canvas canvas )
    {
        super.draw(canvas);
        usernameText.draw(canvas);
    }


    @Override
    public void updateState(HashMap<String, String> updateMap) {
        for(Map.Entry<String,String> entry : updateMap.entrySet() ){
            switch (entry.getKey()){
                case "username":
                    usernameText.setText(entry.getValue());
                    break;
                case "hp":
                    hp = Integer.parseInt( entry.getValue() );
            }
        }
    }

    @Override
    protected void useItem() {
        switch ( getEquippedItem() ) {
            case BOMB:
                new Packet00Spawn(
                        NetworkEntities.BOMB.getClassId(), NetworkEntities.BOMB, Orientation.DOWN, getNextCurrentCells().get(0)
                ).writeData( connection );
                break;
            case SWORD:
                super.useItem();
                break;
            case BOW:
                new Packet00Spawn(
                        NetworkEntities.BOW.getClassId(), NetworkEntities.BOW, getOrientation(), getNextCurrentCells().get(0)
                ).writeData( connection );
                break;
            case STAFF:
                new Packet00Spawn(
                        NetworkEntities.STAFF.getClassId(), NetworkEntities.STAFF, getOrientation(), getNextCurrentCells().get(0)
                ).writeData( connection );
                break;
        }
    }

    @Override
    public void giveDamage(float damage)
    {
        super.giveDamage( damage );
        var updateMap = new HashMap<String,String>();
        updateMap.put( "hp", String.valueOf( getHp() ) );
        new Packet03Update( getId(), updateMap );
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
        HashMap initalState = new HashMap();
        initalState.put( "username", usernameText.getText() );
        return new Packet00Spawn( getId(), NetworkEntities.PLAYER, getOrientation(), getCurrentCells().get(0), initalState );
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

    @Override
    public boolean wantsCellInteraction()
    {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v)
    {
        ((NARPGInteractionVisitor)v).interactWith( this );
    }

    @Override
    public void interactWith(Interactable other)
    {
        if ( connection.isServer() )
        {
            super.interactWith( other );
        }
    }

    class NetworkARPGPlayerHandler implements NARPGInteractionVisitor
    {

        @Override
        public void interactWith( NetworkARPGPlayer player )
        {
            if ( state != PlayerStates.IDLE && getEquippedItem().getVuln() == Vulnerabilities.CLOSE_RANGE )
            {
                System.out.println(getHp());
                player.giveDamage( getEquippedItem().getDamage() );
            }
        }

        @Override
        public void interactWith( NetworkCoin coin ) {
            coin.collect();
            HashMap<String, String> changeMap = new HashMap();
            changeMap.put("playerMoney", String.valueOf(getMoney() + coin.getValue()));
            var updatePacket = new Packet03Update(getId(), changeMap);
            updatePacket.writeData(connection);
        }

        public void interactWith( NetworkHeart heart )
        {
            heart.collect();
        }
    }
}
