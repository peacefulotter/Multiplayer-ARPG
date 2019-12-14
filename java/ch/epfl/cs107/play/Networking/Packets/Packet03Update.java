package ch.epfl.cs107.play.Networking.Packets;

import ch.epfl.cs107.play.Networking.Connection;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Packet03Update extends Packet {
    private final static int packetId = 03;
    private HashMap beanMap = new HashMap();

    public Packet03Update(int objectId, HashMap beanMap) {
        super(packetId, objectId);
        this.beanMap = beanMap;
    }

    public Packet03Update(byte[] data) {
        super(packetId, data);
        String[] dataArray = readData(data).split(",");
        //converting string of a HashMap to an actual hash map as seen here : https://stackoverflow.com/questions/3957094/convert-hashmap-tostring-back-to-hashmap-in-java
        String hashMapString = dataArray[1];
        Properties props = new Properties();
        try {
            props.load(new StringReader(hashMapString.substring(1, hashMapString.length() - 1).replace(", ", "\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        beanMap = new HashMap<String, String>();
        for (Map.Entry<Object, Object> e : props.entrySet()) {
            beanMap.put((String) e.getKey(), (String) e.getValue());
        }
    }

    @Override
    public void writeData(Connection connection) {
        connection.sendData(getData());
    }

    public HashMap getBeanMap() {
        return beanMap;
    }

    @Override
    public byte[] getData() {
        return ("03" + objectId + "," + (beanMap)).getBytes();
    }
}
