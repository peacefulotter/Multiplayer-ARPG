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
    private PrintWriter out;
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
        out.println(data);
    }
    private void processIncomingData(InputStream inStream, OutputStream outStream) throws IOException{
        String msg;
        var in= new Scanner(inStream, StandardCharsets.UTF_8);
        out= new PrintWriter(new OutputStreamWriter(outStream,StandardCharsets.UTF_8),true);
        boolean done=false;
        while (!done && in.hasNextLine()) { //in = new BufferedReader(new
            // InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            msg=in.nextLine();
            if(isServer){
                System.out.println(msg);
                game.updatePlayerState(Integer.parseInt(msg));
            }
            if(msg.trim().equals("BYE")){
                out.println("bye");
                done=true;
            }
        }
    }
}
