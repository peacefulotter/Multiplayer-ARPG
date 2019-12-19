package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;

import java.util.HashMap;

public class Packet06Despawn extends Packet
{
    private final static int packetId = 03;
    private HashMap despawnMap;

    public Packet06Despawn( int objectId)
    {
        super( packetId, objectId );
    }

    public Packet06Despawn(byte[] data)
    {
        super(packetId, data);
    }

    @Override
    public void writeData(Connection connection)
    {
            connection.sendData(getData());
    }


    @Override
    public byte[] getData() {
            return ("06" + objectId + ";" + (despawnMap)).getBytes();
        }
}
