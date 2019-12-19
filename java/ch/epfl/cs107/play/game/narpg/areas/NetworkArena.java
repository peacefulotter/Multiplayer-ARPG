package ch.epfl.cs107.play.game.narpg.areas;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.arpg.area.ARPGArea;
import ch.epfl.cs107.play.game.narpg.announcement.ServerAnnouncement;

import java.util.ArrayList;
import java.util.List;

public class NetworkArena extends ARPGArea {
    private final Connection connection;
    private final boolean isServer;
    private final List<NetworkEntity> networkEntities;
    private ServerAnnouncement announcement;

    public NetworkArena(Connection connection, boolean isServer) {
        networkEntities = new ArrayList<>();
        this.connection = connection;
        this.isServer = isServer;
        announcement = new ServerAnnouncement();
    }

    public ServerAnnouncement getAnnouncement()
    {
        return announcement;
    }

    public List<NetworkEntity> getNetworkEntities() {
        return networkEntities;
    }

    @Override
    protected void createArea() {
        // load the background for the client and server
        registerActor(new Background(this));
        registerActor( announcement );
    }

    @Override
    public String getTitle() {
        return "custom/Arena";
    }

    @Override
    public float getCameraScaleFactor() {
        if (isServer) {
            return 30;
        }
        return SCALE_FACTOR + 3;
    }

    public boolean registerActor(NetworkEntity networkEntity) {
        boolean registered = registerActor((Actor) networkEntity);
        if (registered) {
            networkEntities.add(networkEntity);
        }
        return registered;
    }

    public void unregisterActor(NetworkEntity networkEntity) {
        unregisterActor((Actor) networkEntity);
        networkEntities.remove(networkEntity);
    }

    public void unregisterActor(List<NetworkEntity> networkEntityList) {
        for (NetworkEntity e : networkEntityList) {
            if(e==null){
                System.out.println("null entity unregister");
                return;
            }
            unregisterActor((Actor)e);
            networkEntities.remove(e);
        }
    }

    @Override
    public void end() {
        System.out.println("ending by area end");
        super.end();
    }

}
