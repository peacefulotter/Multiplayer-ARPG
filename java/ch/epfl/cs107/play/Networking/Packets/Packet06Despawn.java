package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;

import java.util.HashMap;

public class Packet06Despawn extends Packet
{
    private final static int packetId = 03;
    private HashMap despawnMap;

    public Packet06Despawn( int objectId, HashMap updateMap)
    {
        super( packetId, objectId );
        this.despawnMap = updateMap;
    }

    public Packet06Despawn(byte[] data)
    {
        super(packetId, data);
        String[] dataArray = readData(data).split(";");
        despawnMap = Packet.getHashMapFromString(dataArray[1]);
    }

    @Override
    public void writeData(Connection connection)
    {
            connection.sendData(getData());
    }

    public HashMap getUpdateMap() {
            return despawnMap;
        }

    @Override
    public byte[] getData() {
            return ("06" + objectId + ";" + (despawnMap)).getBytes();
        }
}
