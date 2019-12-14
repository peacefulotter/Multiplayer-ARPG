package ch.epfl.cs107.play.Networking;

public interface Connection{
     boolean isServer();
     void sendData(byte[] data);
     void sendDataTo(long mainId,byte[] data);
}
