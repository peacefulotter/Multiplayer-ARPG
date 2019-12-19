package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;

public class Packet04Chat extends Packet
{
    private final static int packetId = PacketTypes.TCHAT.getPacketID();
    private final String text;

    public Packet04Chat(String text )
    {
        super(packetId,0);
        this.text = text;
    }

    public Packet04Chat(byte[] data)
    {
        super(data);
        System.out.println(readData(data).split(";")[1]);
        this.text = readData(data).split(";")[1];
    }

    public String getText() { return text; }

    @Override
    public void writeData(Connection connection)
    {
        connection.sendData( getData() );
    }

    @Override
    public byte[] getData()
    {
        return ("04"+0+";" + text ).getBytes();
    }
}
