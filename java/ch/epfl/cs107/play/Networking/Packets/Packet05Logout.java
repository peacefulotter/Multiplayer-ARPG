package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;

public class Packet05Logout extends Packet
{
        private long connectionId;
        private String username;

        public Packet05Logout( long connectionId, String username )
        {
            super(05, 0);
            this.connectionId = connectionId;
            this.username = username;
        }

        public Packet05Logout( byte[] data )
        {
            super(05, 0);
            String[] dataArray = readData(data).split(";");
            connectionId = Long.parseLong(dataArray[1]);
            username = dataArray[2];
        }

        public long getConnectionId() {
            return connectionId;
        }
        public String getUsername(){
            return username;
        }

        @Override
        public void writeData(Connection connection) {
            connection.sendDataTo(connectionId, getData() );
        }

        @Override
        public byte[] getData() {
            return ("01" + 0 + ";" + connectionId + ";" + username).getBytes();
        }
}
