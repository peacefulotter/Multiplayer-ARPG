package ch.epfl.cs107.play.game.narpg.actor;

import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.handler.NARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.HashMap;

public class NetworkBomb extends Bomb implements NetworkEntity {
    private final int spawnedBy;
    public NetworkBomb(Area area, Orientation orientation, DiscreteCoordinates position, int spawnedBy ) {
        super(area, orientation, position);
        handler = new NetworkBombHandler();
        this.spawnedBy=spawnedBy;
    }
    public NetworkBomb(Area area, Orientation orientation, DiscreteCoordinates position, HashMap<String, String> initialState) {
       this(area,orientation,position,Integer.parseInt(initialState.get("spawnedBy")));
    }
    @Override
    public int getId() {
        return NetworkEntities.BOMB.getClassId();
    }


    @Override
    public Packet00Spawn getSpawnPacket() {
        var initialState = new HashMap<String, String>();
        initialState.put("spawnedBy", String.valueOf(spawnedBy));
        return new Packet00Spawn(getId(), NetworkEntities.BOMB, getOrientation(), getCurrentMainCellCoordinates(), initialState);
    }


    @Override
    public void updateState(HashMap<String, String> updateMap) {

    }

    class NetworkBombHandler implements NARPGInteractionVisitor
    {
        @Override
        public void interactWith(NetworkARPGPlayer player)
        {
            System.out.println("bomb interact");
        }
    }
}
