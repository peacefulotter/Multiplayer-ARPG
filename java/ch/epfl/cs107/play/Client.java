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
    private final String username;
    private ConnectionHandler connection;

    public Client(String adress, int port, String username) {
        boolean connected = false;
        this.username=username;
        while (!connected) {
            try {
                Socket incoming = new Socket(adress, port);
                NARPG game = new NARPG(false, this);
                connection = new ConnectionHandler(incoming, game, false, this, mainId,username);
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
        String address=args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];
        new Client(address,port,username);
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
        var loginPacket = new Packet01Login(mainId,username);
        loginPacket.writeData(this);
    }

    public String getUsername() {
        return username;
    }
}
