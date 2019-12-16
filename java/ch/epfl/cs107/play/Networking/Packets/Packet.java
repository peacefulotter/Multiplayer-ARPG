package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class Packet {
    public byte packetId;
    protected int objectId;

    public Packet(int packetId, int objectId) {
        this.packetId = (byte) packetId;
        this.objectId = objectId;
    }
    public Packet(int packetId,byte[] data){
        String message[]=readData(data).split(";");
        this.objectId= Integer.parseInt(message[0]);
    }

    public static PacketTypes lookupPacket(String packetId){
        try{
            return lookUpPacket(Integer.parseInt(packetId));
        }catch (NumberFormatException e){
            return PacketTypes.INVALID;
        }
    }
    public static PacketTypes lookUpPacket(int id) {
        for (PacketTypes p : PacketTypes.values()) {
            if (p.getPacketID() == id) {
                return p;
            }
        }
        return PacketTypes.INVALID;
    }

    public int getObjectId() {
        return objectId;
    }

    public abstract void writeData(Connection connection);

    //converting string of a HashMap to an actual hash map as seen here : https://stackoverflow.com/questions/3957094/convert-hashmap-tostring-back-to-hashmap-in-java
    public static HashMap<String,String> getHashMapFromString(String hashMapString){
        Properties props = new Properties();
        try {
            props.load(new StringReader(hashMapString.substring(1, hashMapString.length() - 1).replace(", ", "\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        var hashMap = new HashMap<String, String>();
        for (Map.Entry<Object, Object> e : props.entrySet()) {
            hashMap.put((String) e.getKey(), (String) e.getValue());
        }
        return hashMap;
    }

    public void writeData(Connection connection, long connectionId){
        connection.sendDataTo(connectionId,getData());
    }

    public String readData(byte[] data) {
        String message = new String(data).trim();
        return message.substring(2);
    }

    public abstract byte[] getData();

    public static enum PacketTypes {
        INVALID(-1),
        SPAWN(00),
        LOGIN(01),
        MOVE(02),
        UPDATE(03),
        TCHAT( 04 );

        private int packetID;

        PacketTypes(int packetID) {
            this.packetID = packetID;
        }

        public int getPacketID() {
            return packetID;
        }

    }
}
