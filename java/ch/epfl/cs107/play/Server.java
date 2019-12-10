package ch.epfl.cs107.play;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.ConnectionHandler;
import ch.epfl.cs107.play.Networking.EchoThreadedHandler;
import ch.epfl.cs107.play.game.Game;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.arpg.NARPG;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Connection {
    private List<ConnectionHandler> connections = new ArrayList<ConnectionHandler>();

    public Server(int port) {
        NARPG game = new NARPG(true);
        Thread GameThread = new Thread(new ThreadedPlay(game));
        GameThread.start();
        try {
            ServerSocket server = new ServerSocket(port);
            int num = 1;
            while (true) {
                Socket incoming = server.accept();
                System.out.println("Welcome : " + num);
                ConnectionHandler handler = new ConnectionHandler(incoming, game, true);
                connections.add(handler);
                Thread thread = new Thread(handler);
                thread.start();
                num++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server(Integer.parseInt(args[0]));
    }

    public void sendDataToAllClients(byte[] data) {
        for (ConnectionHandler c : connections) {
            c.sendData(data);
        }
    }

    @Override
    public boolean isServer() {
        return true;
    }

    @Override
    public void sendData(byte[] data) {
        sendDataToAllClients(data);
    }
}

