package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Client;
import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.utils.OrientationValues;
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


}
