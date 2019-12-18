package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;

import java.util.HashMap;

public class Packet03Update extends Packet {
    private final static int packetId = 03;
    private HashMap updateMap = new HashMap();

    public Packet03Update( int objectId, HashMap updateMap)
    {
        super( packetId, objectId );
        this.updateMap = updateMap;
    }

    public Packet03Update(byte[] data) {
        super(packetId, data);
        String[] dataArray = readData(data).split(";");
        updateMap = Packet.getHashMapFromString(dataArray[1]);
        System.out.println(getObjectId() + " packet ");
    }

    @Override
    public void writeData(Connection connection) {
        connection.sendData(getData());
    }

    public HashMap getUpdateMap() {
        return updateMap;
    }

    @Override
    public byte[] getData() {
        return ("03" + objectId + ";" + (updateMap)).getBytes();
    }
}
