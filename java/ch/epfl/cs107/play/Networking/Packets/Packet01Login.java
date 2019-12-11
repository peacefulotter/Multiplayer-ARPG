package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;

public class Packet01Login extends Packet {

    private long connectionId;

    public Packet01Login(long connectionId) {
        super(01, 0);
        this.connectionId = connectionId;
    }

    public Packet01Login(byte[] data) {
        super(01, 0);
        String[] dataArray = readData(data).split(",");
        connectionId = Long.parseLong(dataArray[1]);
    }

    public long getConnectionId() {
        return connectionId;
    }

    @Override
    public void writeData(Connection connection) {
        connection.sendDataTo(connectionId, getData());
    }

    @Override
    public byte[] getData() {
        return ("01" + 0 + "," + connectionId).getBytes();
    }
}
