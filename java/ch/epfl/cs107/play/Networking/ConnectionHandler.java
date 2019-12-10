package ch.epfl.cs107.play.Networking;


import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.arpg.NARPG;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class ConnectionHandler implements Runnable{
    private boolean isServer;
    private NARPG game;
    private Socket socket;
    private OutputStream out;
    private BufferedReader in;
    public ConnectionHandler(Socket socket, NARPG arpg, boolean isServer){
        this.socket=socket;
        this.isServer=isServer;
        this.game=arpg;
    }

    @Override
    public void run() {
        try(InputStream inStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream()){
            processIncomingData(inStream, outStream);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void sendData(String data){
        sendData(data.getBytes());
    }
    public void sendData(byte[] data){
        try{
            var dos=new DataOutputStream(out);
            dos.writeInt(data.length);
            dos.write(data,0,data.length);
        }catch (IOException e){
            e.printStackTrace();
            e.printStackTrace();        }
    }
    private void processIncomingData(InputStream inStream, OutputStream outStream) throws IOException{
        out= outStream;
        boolean done=false;
        while (!done) { //in = new BufferedReader(new
            // InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            DataInputStream dis= new DataInputStream(inStream);
            int len=dis.readInt();
            byte[] data =new byte[len];
            if(len>0){
                dis.readFully(data);
            }

            if(isServer){
                System.out.println(data.toString());
            }
        }
    }
}
