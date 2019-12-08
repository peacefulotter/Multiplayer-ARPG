package ch.epfl.cs107.play;

import ch.epfl.cs107.play.Networking.ConnectionHandler;
import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.arpg.NARPG;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Client extends Play {

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);

        try {
            Socket incoming = new Socket("localhost", port);
            NARPG game = new NARPG(false);
            ConnectionHandler connectionHandler = new ConnectionHandler(incoming, game, false);
            Thread connectionThread = new Thread(connectionHandler);
            Thread GameThread = new Thread(new ThreadedPlay(game));
            game.setConnectionHandler(connectionHandler);
            connectionThread.start();
            GameThread.start();
        } catch (IOException e) {

        }
    }
}
