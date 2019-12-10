package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Client;
import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Server;

public class Packet00Login extends Packet {
    private String username;

    public Packet00Login(byte[] data,int objectId) {
        super(00,objectId);
        this.username = readData(data);
    }

    public Packet00Login(String username,int objectId) {
        super(00,objectId);
        this.username = username;
    }

    @Override
    public void writeData(Connection connection) {
        connection.sendData(getData());
    }

    @Override
    public byte[] getData() {
        return ("00" + username).getBytes();
    }

}
