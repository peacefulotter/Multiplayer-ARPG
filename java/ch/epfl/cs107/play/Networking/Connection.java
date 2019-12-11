package ch.epfl.cs107.play.Networking;

import ch.epfl.cs107.play.Networking.Packets.Packet;

public interface Connection{
     boolean isServer();
     void sendData(byte[] data);
     void sendDataTo(long mainId,byte[] data);
}
