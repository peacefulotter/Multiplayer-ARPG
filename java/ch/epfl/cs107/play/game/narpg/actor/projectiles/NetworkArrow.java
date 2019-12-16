package ch.epfl.cs107.play.game.narpg.actor.projectiles;

import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.narpg.actor.NetworkBomb;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.handler.NARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.HashMap;
import java.util.Map;

public class NetworkArrow extends Arrow implements NetworkEntity, NetworkProjectile {
    private enum stateProperties{
        MAX_DISTANCE("maxDistance"),
        SPEED("speed"),
        SPAWNED_BY("spawnedBy");

        stateProperties(String spawnedBy) {
        }
    }
    private final int spawnedBy;

    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param speed
     * @param maxDistance
     */
    public NetworkArrow(Area area, Orientation orientation, DiscreteCoordinates position, int speed, int maxDistance,HashMap<String,String> initialState) {
        super(area, orientation, position, speed, maxDistance);
        handler = new NetworkArrowHandler();
        this.spawnedBy = Integer.parseInt(initialState.get("spawnedBy"));

    }


    @Override
    public int getId() {
        return NetworkEntities.BOW.getClassId();
    }


    @Override
    public Packet00Spawn getSpawnPacket() {
        var initialState = new HashMap<String, String>();
        initialState.put("spawnedBy", String.valueOf(spawnedBy));
        return new Packet00Spawn(getId(), NetworkEntities.BOW, getOrientation(), getCurrentMainCellCoordinates(), initialState);
    }

    @Override
    public void updateState(HashMap<String, String> updateMap) {

    }

    @Override
    public int getSpawnerId() {
        return spawnedBy;
    }

    class NetworkArrowHandler implements NARPGInteractionVisitor {
        @Override
        public void interactWith(NetworkARPGPlayer player) {
            stopProjectile();
            player.giveDamage(1f);
        }

        @Override
        public void interactWith(NetworkBomb bomb) {
            stopProjectile();
            bomb.explode();
        }
    }
}
