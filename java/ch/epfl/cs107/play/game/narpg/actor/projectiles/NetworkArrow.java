package ch.epfl.cs107.play.game.narpg.actor.projectiles;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.Networking.Packets.Packet06Despawn;
import ch.epfl.cs107.play.Networking.utils.IdGenerator;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.handler.NARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.HashMap;

public class NetworkArrow extends Arrow implements NetworkEntity, NetworkProjectile {
    private final int spawnedBy;
    private Connection connection;
    private final int id;
    private final float arrowDamage;
    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param speed
     * @param maxDistance
     */

    public NetworkArrow(Area area, Orientation orientation, DiscreteCoordinates position, Connection connection, int speed, float maxDistance, float arrowDamage, int spawnedBy, int id ) {
        super(area, orientation, position, speed, maxDistance);
        handler = new NetworkArrowHandler();
        this.spawnedBy = spawnedBy;
        this.connection=connection;
        if ( id == 0 )
        {
            this.id = IdGenerator.generateId();
        } else {
            this.id = id;
        }
        this.arrowDamage = arrowDamage;
    }

    public NetworkArrow(Area area, Orientation orientation, DiscreteCoordinates position, Connection connection, HashMap<String, String> initialState, int id) {
        this(area, orientation, position, connection,
                Integer.parseInt(initialState.get(stateProperties.SPEED.toString())),
                Float.parseFloat(initialState.get(stateProperties.MAX_DISTANCE.toString())),
                Float.parseFloat(initialState.get(stateProperties.DAMAGE.toString()) ),
                Integer.parseInt(initialState.get(stateProperties.SPAWNED_BY.toString())),
                id );
    }

    @Override
    public int getId() {
        return id;
    }


    @Override
    public Packet00Spawn getSpawnPacket() {
        HashMap initialState = new HashMap<String, String>();
        initialState.put(stateProperties.SPAWNED_BY.toString(),String.valueOf(spawnedBy));
        initialState.put(stateProperties.MAX_DISTANCE.toString(),String.valueOf(getMaxDistance()));
        initialState.put(stateProperties.SPEED.toString(),String.valueOf(getSpeed()));
        initialState.put(stateProperties.DAMAGE.toString(), String.valueOf( arrowDamage ));
        initialState.put(stateProperties.DAMAGE.toString(), String.valueOf( arrowDamage ));

        return new Packet00Spawn(id, NetworkEntities.BOW, getOrientation(), getCurrentMainCellCoordinates(), initialState);
    }

    @Override
    public void updateState(HashMap<String, String> updateMap) {
    }

    @Override
    public void interactWith(Interactable other) {
        if (connection.isServer()) {
            other.acceptInteraction(handler);
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((NARPGInteractionVisitor)v).interactWith(this);
    }

    @Override
    public int getSpawnerId() {
        return spawnedBy;
    }

    private enum stateProperties {
        MAX_DISTANCE("maxDistance"),
        SPEED("speed"),
        DAMAGE( "damage" ),
        SPAWNED_BY("spawnedBy");

        private final String property;

        stateProperties(String spawnedBy) {
            this.property = spawnedBy;
        }

        public String getProperty() {
            return property;
        }
    }

    class NetworkArrowHandler implements NARPGInteractionVisitor {
        @Override
        public void interactWith(NetworkARPGPlayer player) {
            if(player.getId()==spawnedBy) {
                return;
            }
<<<<<<< HEAD
            player.giveDamage(arrowDamage, spawnedBy);
            System.out.println(id);
=======
            player.giveDamage(1f,spawnedBy);
>>>>>>> 129a128ee6407b41b22d1ecbda65b3994fb7eeee
            HashMap<String, String> despawnMap = new HashMap<>();
            new Packet06Despawn( id, despawnMap ).writeData( connection );
            stopProjectile();
        }
    }
}
