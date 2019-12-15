package ch.epfl.cs107.play;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.ConnectionHandler;
import ch.epfl.cs107.play.Networking.Packets.Packet01Login;
import ch.epfl.cs107.play.game.narpg.NARPG;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Client extends Play implements Connection {
    private final static long mainId = new Random().nextLong();
    private ConnectionHandler connection;

    public Client(int port) {
        boolean connected = false;
        while (!connected) {
            try {
                Socket incoming = new Socket("localhost", port);
                NARPG game = new NARPG(false, this);
                connection = new ConnectionHandler(incoming, game, false, this, mainId);
                Thread connectionThread = new Thread(connection);
                Thread GameThread = new Thread(new ThreadedPlay(game,false));
                connectionThread.start();
                GameThread.start();
                connected=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        new Client(port);
    }

    @Override
    public boolean isServer() {
        return false;
    }

    @Override
    public void sendData(byte[] data) {
        connection.sendData(data);
    }
    public void sendDataTo(long mainId, byte[] data){
        connection.sendData(data);
    }

    public void login(){
        var loginPacket = new Packet01Login(mainId);
        loginPacket.writeData(this);
    }
}
