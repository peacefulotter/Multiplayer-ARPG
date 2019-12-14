package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Client;
import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Server;

public abstract class Packet {
    public byte packetId;
    protected int objectId;

    public Packet(int packetId, int objectId) {
        this.packetId = (byte) packetId;
        this.objectId = objectId;
    }
    public Packet(int packetId,byte[] data){
        String message[]=readData(data).split(",");
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
        UPDATE(10);

        private int packetID;

        PacketTypes(int packetID) {
            this.packetID = packetID;
        }

        public int getPacketID() {
            return packetID;
        }

    }
}
