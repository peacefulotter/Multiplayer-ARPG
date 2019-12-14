package ch.epfl.cs107.play.game.narpg.actor.monster;

import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.monster.LogMonster;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class NetworkLogMonster extends LogMonster implements NetworkEntity
{
    public NetworkLogMonster(Area area, DiscreteCoordinates coords)
    {
        super(area, coords);
    }

    @Override
    public int getId() {
        return NetworkEntities.LOG_MONSTER.getClassId();
    }

    @Override
    public void setPosition(DiscreteCoordinates position) {

    }

    @Override
    public void setOrientation(Orientation orientation) {

    }

    @Override
    public Packet00Spawn getSpawnPacket() {
        System.out.println("Spawned LogMonster");
        return null;
    }
}
