package ch.epfl.cs107.play;

import ch.epfl.cs107.play.Networking.ConnectionHandler;
import ch.epfl.cs107.play.Networking.EchoThreadedHandler;
import ch.epfl.cs107.play.game.Game;
import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.arpg.NARPG;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSetup {

    public static void main(String[] args) {
        NARPG game = new NARPG(true);
        int port = Integer.parseInt(args[0]);
        Thread GameThread= new Thread(new ThreadedPlay(game));
        GameThread.start();
        try {
            ServerSocket server = new ServerSocket(port);
            int num=1;
            while(true){
                Socket incoming = server.accept();
                System.out.println("Welcome : "+num);
                ConnectionHandler handler= new ConnectionHandler(incoming, game,true);
                Thread thread = new Thread(handler);
                thread.start();
                num++;
            }

        } catch (IOException e) {

        }
    }
}

