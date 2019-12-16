package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.utils.OrientationValues;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

import java.util.HashMap;

public class Packet00Spawn extends Packet {
    private NetworkEntities object;
    private Orientation orientation;
    private int startX;
    private int startY;
    private HashMap<String,String> initialState;

    public Packet00Spawn(byte[] data) {
        super(00, data);
        String[] dataArray = readData(data).split(";");
        this.object = NetworkEntities.lookUpEntity(Integer.parseInt(dataArray[1]));
        this.orientation= OrientationValues.getOrientationByValue(Integer.parseInt(dataArray[2]));
        this.startX=Integer.parseInt(dataArray[3]);
        this.startY=Integer.parseInt(dataArray[4]);
        this.initialState=Packet.getHashMapFromString(dataArray[5]);
    }

    public Packet00Spawn(int objectId, NetworkEntities networkEntity, Orientation orientation, DiscreteCoordinates startPosition, HashMap<String,String> initialState) {
        super(00, objectId);
        this.object=networkEntity;
        this.orientation = orientation;
        this.startX=startPosition.x;
        this.startY=startPosition.y;
        this.initialState=initialState;
    }
    public Packet00Spawn(int objectId, NetworkEntities networkEntity, Orientation orientation, DiscreteCoordinates startPosition) {
        this(objectId,networkEntity,orientation,startPosition,new HashMap<>());
    }


    public NetworkEntities getObject() {
        return object;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public DiscreteCoordinates getDiscreteCoordinate(){
        return new DiscreteCoordinates(startX, startY);
    }

    public HashMap<String, String> getInitialState() {
        return initialState;
    }

    @Override
    public void writeData(Connection connection) {
        connection.sendData(getData());
    }

    @Override
    public byte[] getData() {
        return ("00" + objectId + ";"+object.getClassId()+";"+ OrientationValues.getOrientationValue(orientation) + ";" + this.startX+";"
                + this.startY+";"+initialState.toString()).getBytes();
    }

}
