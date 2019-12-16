package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Client;
import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.utils.OrientationValues;
import ch.epfl.cs107.play.Server;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Packet02Move extends Packet {
    private Orientation orientation;
    private int startX;
    private int startY;
    private int speed;
    public Packet02Move(int objectId, Orientation orientation, DiscreteCoordinates start, int speed) {
        super(02, objectId);
        this.objectId = objectId;
        this.orientation = orientation;
        this.startX = start.x;
        this.startY = start.y;
        this.speed = speed;
    }

    public Packet02Move(byte[] data) {
        super(02, data);
        String[] dataArray = readData(data).split("|");
        this.orientation = OrientationValues.getOrientationByValue(Integer.parseInt(dataArray[1]));
        this.startX = Integer.parseInt(dataArray[2]);
        this.startY = Integer.parseInt(dataArray[3]);
        this.speed = Integer.parseInt(dataArray[4]);
    }

    public int getSpeed() {
        return speed;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public DiscreteCoordinates getStart() {
        return new DiscreteCoordinates(startX, startY);
    }

    @Override
    public void writeData(Connection connection) {
        connection.sendData(getData());
    }

    @Override
    public byte[] getData() {
        return ("02" + this.objectId + ";"
                + OrientationValues.getOrientationValue(this.orientation) + ";"
                + this.startX + ";"
                + this.startY + ";" + this.speed).getBytes();
    }


}
