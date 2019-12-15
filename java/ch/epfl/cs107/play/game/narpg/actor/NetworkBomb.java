package ch.epfl.cs107.play.game.narpg.actor;

import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class NetworkBomb extends Bomb implements NetworkEntity {
    public NetworkBomb(Area area, Orientation orientation, DiscreteCoordinates position ) {
        super(area, orientation, position);
    }

    @Override
    public int getId() {
        return NetworkEntities.BOMB.getClassId();
    }


    @Override
    public Packet00Spawn getSpawnPacket() {
        return null;
    }

    class NetworkBombHandler implements ARPGInteractionVisitor
    {

    }
}
