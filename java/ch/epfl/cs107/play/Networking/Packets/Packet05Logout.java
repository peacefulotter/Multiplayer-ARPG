package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;

public class Packet05Logout extends Packet
{
        private long connectionId;

        public Packet05Logout( int objectId,long connectionId)
        {
            super(05, objectId);
            this.connectionId = connectionId;
        }

        public Packet05Logout( byte[] data )
        {
            super(05, data);
            String[] dataArray = readData(data).split(";");
            connectionId = Long.parseLong(dataArray[1]);
        }

        public long getConnectionId() {
            return connectionId;
        }
        @Override
        public void writeData(Connection connection) {
            connection.sendDataTo(connectionId, getData() );
        }

        @Override
        public byte[] getData() {
            return ("05" + objectId + ";" + connectionId).getBytes();
        }
}
