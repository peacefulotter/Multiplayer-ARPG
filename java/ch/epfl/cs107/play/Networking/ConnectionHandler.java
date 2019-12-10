package ch.epfl.cs107.play.Networking;


import ch.epfl.cs107.play.Networking.Packets.Packet;
import ch.epfl.cs107.play.Networking.Packets.Packet.PacketTypes;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.Server;
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

    public ConnectionHandler(Socket socket, NARPG arpg, boolean isServer, Connection connection) {
        this.socket = socket;
        this.isServer = isServer;
        this.game = arpg;
        this.connection=connection;
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

    public void sendData(String data) {
        sendData(data.getBytes());
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

            if (isServer) {
                ((Server)connection).sendDataToAllClients(data);
            }
            parsePacket(data);
        }
    }
    private void parsePacket(byte[] data){
        String message=new String(data).trim();
        PacketTypes type= Packet.lookupPacket(message.substring(0,2));
        switch(type){
            default:
            case INVALID:
                break;
            case LOGIN:
                Packet00Spawn packet= new Packet00Spawn(data);
                game.spawnObject(packet);
                break;
            case DISCONNECT:
                break;
        }

    }

}
