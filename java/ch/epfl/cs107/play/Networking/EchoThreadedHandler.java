package ch.epfl.cs107.play.Networking;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class EchoThreadedHandler implements Runnable {
    private Socket incoming;
    public EchoThreadedHandler(Socket incoming){
        this.incoming=incoming;
    }
    @Override
    public void run() {
        try(InputStream inStream = incoming.getInputStream();
            OutputStream outStream = incoming.getOutputStream()){
            process(inStream, outStream);

        }catch (IOException e){

        }
    }
    private void process(InputStream inStream, OutputStream outStream) throws IOException{
        String msg;
        Scanner in= new Scanner(inStream, StandardCharsets.UTF_8);
        PrintWriter out= new PrintWriter(new OutputStreamWriter(outStream,StandardCharsets.UTF_8),true);
        boolean done=false;
        while (!done && in.hasNextLine()) { //in = new BufferedReader(new
            // InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            msg=in.nextLine();
            out.println("Echo : "+ msg);
            if(msg.trim().equals("BYE")){
                out.println("bye");
                done=true;
            }
        }
    }
}
