package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;

public class Packet05Logout extends Packet
{
        private final long connectionId;

        public Packet05Logout( int objectId,long connectionId)
        {
            super(05, objectId);
            this.connectionId = connectionId;
        }

        public Packet05Logout( byte[] data )
        {
            super(data);
            String[] dataArray = readData(data).split(";");
            connectionId = Long.parseLong(dataArray[1]);
        }

        public long getConnectionId() {
            return connectionId;
        }
        @Override
        public void writeData(Connection connection) {
            connection.sendData(getData());
        }

        @Override
        public byte[] getData() {
            return ("05" + objectId + ";" + connectionId).getBytes();
        }
}
