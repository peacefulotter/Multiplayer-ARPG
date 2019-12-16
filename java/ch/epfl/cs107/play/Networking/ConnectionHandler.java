package ch.epfl.cs107.play.Networking;


import ch.epfl.cs107.play.Networking.Packets.*;
import ch.epfl.cs107.play.Networking.Packets.Packet.PacketTypes;
import ch.epfl.cs107.play.game.narpg.NARPG;

import java.io.*;
import java.net.Socket;


public class ConnectionHandler implements Runnable {
    private boolean isServer;
    private NARPG game;
    private Socket socket;
    private OutputStream out;
    private BufferedReader in;
    private Connection connection;
    private long connectionId;
    private String username;

    public ConnectionHandler(Socket socket, NARPG arpg, boolean isServer, Connection connection, long connectionId, String username) {
        this.socket = socket;
        this.isServer = isServer;
        this.game = arpg;
        this.connection = connection;
        this.connectionId = connectionId;
        this.username = username;
    }

    public ConnectionHandler(Socket socket, NARPG arpg, boolean isServer, Connection connection) {
        this(socket, arpg, isServer, connection, 0l, "");
    }

    public long getConnectionId() {
        return connectionId;
    }

    @Override
    public void run() {
        try (InputStream inStream = socket.getInputStream();
             OutputStream outStream = socket.getOutputStream()) {
            processIncomingData(inStream, outStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendData(byte[] data) {
        try {
            var dos = new DataOutputStream(out);
            dos.writeInt(data.length);
            dos.write(data, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processIncomingData(InputStream inStream, OutputStream outStream) throws IOException {
        out = outStream;
        boolean done = false;
        while (!done) { //in = new BufferedReader(new
            // InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            DataInputStream dis = new DataInputStream(inStream);

            int len = dis.readInt();
            byte[] data = new byte[len];
            if (len > 0) {
                dis.readFully(data);
            }

            parsePacket(data);
        }
    }

    private void parsePacket(byte[] data) {
        String message = new String(data).trim();
        if (message.length() < 2) return;
        PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
        //boolean which decides if data will be sent back to all the clients
        boolean sendDataBackToAll = true;
        switch (type) {
            default:
            case INVALID:
                break;
            case SPAWN:
                Packet00Spawn spawnPacket = new Packet00Spawn(data);
                game.spawnObject(spawnPacket);
                break;
            case LOGIN:
                Packet01Login loginPacket = new Packet01Login(data);
                connectionId = loginPacket.getConnectionId();
                game.login();
                sendDataBackToAll = false;
                break;
            case MOVE:
                Packet02Move movePacket = new Packet02Move(data);
                game.moveObject(movePacket);
                break;
            case UPDATE:
                Packet03Update updatePacket = new Packet03Update(data);
                game.updateObject(updatePacket);
                break;
        }
        if (isServer && sendDataBackToAll) {
            connection.sendData(data);
        }

    }

}
