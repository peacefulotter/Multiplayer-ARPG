package ch.epfl.cs107.play;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.ConnectionHandler;
import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.arpg.NARPG;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Client extends Play implements Connection {
    private ConnectionHandler connection;
    private final String mainId= String.valueOf(new Random().nextLong());

    public Client(int port) {

        try {
            Socket incoming = new Socket("localhost", port);
            NARPG game = new NARPG(false);
            connection = new ConnectionHandler(incoming, game, false);
            Thread connectionThread = new Thread(connection);
            Thread GameThread = new Thread(new ThreadedPlay(game));
            game.setConnection(this);
            connectionThread.start();
            GameThread.start();
        } catch (IOException e) {
            e.printStackTrace();
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
        System.out.println(data);
        connection.sendData(data);
    }
}
