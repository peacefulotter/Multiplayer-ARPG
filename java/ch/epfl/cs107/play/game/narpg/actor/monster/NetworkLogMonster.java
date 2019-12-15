package ch.epfl.cs107.play.game.narpg.actor.monster;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.MovableNetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.Networking.Packets.Packet03Update;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.monster.LogMonster;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class NetworkLogMonster extends LogMonster implements MovableNetworkEntity
{
    private final Area currentArea;
    private final boolean fromServer;
    private final Connection connection;

    public NetworkLogMonster(Area area, DiscreteCoordinates coords, Connection connection, boolean fromServer )
    {
        super(area, coords);
        this.currentArea = area;
        this.connection = connection;
        this.fromServer = fromServer;
    }

    @Override
    public void update(float deltaTime)
    {
        DiscreteCoordinates currentCell = getCurrentCells().get( 0 );
        Orientation currentOrientation = getOrientation();
        LogMonsterState currentState = getState();
        System.out.println(currentCell);
        System.out.println(currentOrientation);
        System.out.println(currentState);
        super.update( deltaTime );
        DiscreteCoordinates nextCell = getCurrentCells().get(0);
        Orientation nextOrientation = getOrientation();
        LogMonsterState nextState = getState();
        System.out.println(nextCell);
        System.out.println(nextOrientation);
        System.out.println(nextState);
        if (
                nextCell.x != currentCell.x || nextCell.y != currentCell.y ||
                
                currentState != nextState

        )
        {
            //HashMap<String,String> changeMap = new HashMap();
            //changeMap.put("state", String.valueOf(state) );
            //Packet03Update updatePacket = new Packet03Update( getId(), changeMap );
            Packet03Update updatePacket = new Packet03Update( getId(), NetworkEntities.LOG_MONSTER, getOrientation(), nextCell, currentArea );
            updatePacket.writeData( connection );
        }
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
        return new Packet00Spawn( getId(), NetworkEntities.LOG_MONSTER, getOrientation(), getCurrentCells().get(0), currentArea );
    }

    @Override
    public void networkMove(Orientation orientation, int Speed, DiscreteCoordinates startPosition)
    {

    }
}
