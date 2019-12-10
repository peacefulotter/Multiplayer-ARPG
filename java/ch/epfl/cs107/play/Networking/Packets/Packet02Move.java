package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Client;
import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Server;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Packet02Move extends Packet {
    private int objectId;
    private Orientation orientation;
    private int startX;
    private int startY;


    public Packet02Move(int objectId, Orientation orientation, DiscreteCoordinates start) {
        super(02,objectId);
        this.objectId = objectId;
        this.orientation = orientation;
        this.startX = start.x;
        this.startY = start.y;
    }

    public Packet02Move(byte[] data) {
        super(02,data);
        String[] dataArray = readData(data).split(",");
        this.objectId = Integer.parseInt(dataArray[0]);
        this.orientation = OrientationValues.getOrientationByValue(Integer.parseInt(dataArray[1]));
        this.startX = Integer.parseInt(dataArray[2]);
        this.startY = Integer.parseInt(dataArray[3]);
    }


    @Override
    public void writeData(Connection connection) {
        connection.sendData(getData());
    }

    @Override
    public byte[] getData() {
        return ("02" + this.objectId + ","
                + OrientationValues.getOrientationValue(this.orientation) + ","
                + this.startX+","
        + this.startY).getBytes();
    }

    private enum OrientationValues {
        UP(0, Orientation.UP), DOWN(1, Orientation.DOWN), LEFT(2, Orientation.LEFT), RIGHT(3, Orientation.RIGHT);
        private int value;
        private Orientation orientation;

        OrientationValues(int value, Orientation orientation) {
            this.value = value;
            this.orientation = orientation;
        }

        public static int getOrientationValue(Orientation orientation) {
            for (OrientationValues o : OrientationValues.values()) {
                if (orientation == o.getOrientation()) return o.getValue();
            }
            return 1;
        }

        public static Orientation getOrientationByValue(int value) {
            for (OrientationValues o : OrientationValues.values()) {
                if (value == o.getValue()) return o.getOrientation();
            }
            return Orientation.DOWN;
        }

        public Orientation getOrientation() {
            return orientation;
        }

        public int getValue() {
            return value;
        }
    }
}
