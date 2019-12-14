package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.utils.OrientationValues;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

public class Packet00Spawn extends Packet {
    private NetworkEntities object;
    private Orientation orientation;
    private int startX;
    private int startY;
    private Area area;

    public Packet00Spawn(byte[] data) {
        super(00, data);
        String[] dataArray = readData(data).split(",");
        this.object = NetworkEntities.lookUpEntity(Integer.parseInt(dataArray[1]));
        this.orientation= OrientationValues.getOrientationByValue(Integer.parseInt(dataArray[2]));
        this.startX=Integer.parseInt(dataArray[3]);
        this.startY=Integer.parseInt(dataArray[4]);
    }

    public Packet00Spawn(int objectId,NetworkEntities networkEntity, Orientation orientation, DiscreteCoordinates startPosition, Area area) {
        super(00, objectId);
        this.object=networkEntity;
        this.orientation = orientation;
        this.startX=startPosition.x;
        this.startY=startPosition.y;
        this.area = area;
    }


    public NetworkEntities getObject() {
        return object;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Area getArea() {
        return area;
    }

    public DiscreteCoordinates getDiscreteCoordinate(){
        return new DiscreteCoordinates(startX, startY);
    }


    @Override
    public void writeData(Connection connection) {
        connection.sendData(getData());
    }

    @Override
    public byte[] getData() {
        return ("00" + objectId + ","+object.getClassId()+","+ OrientationValues.getOrientationValue(orientation) + "," + this.startX+","
                + this.startY+","+this.area.toString()).getBytes();
    }

}
