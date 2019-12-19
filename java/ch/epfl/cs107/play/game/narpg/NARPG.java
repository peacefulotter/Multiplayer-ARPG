package ch.epfl.cs107.play.game.narpg;

import ch.epfl.cs107.play.Client;
import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.Networking.MovableNetworkEntity;
import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.*;
import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.narpg.actor.NetworkBomb;
import ch.epfl.cs107.play.game.narpg.actor.NetworkEntities;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.actor.projectiles.NetworkArrow;
import ch.epfl.cs107.play.game.narpg.actor.projectiles.NetworkMagic;
import ch.epfl.cs107.play.game.narpg.areas.NetworkArena;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.window.Window;

import java.util.*;


public class NARPG extends AreaGame {
    private final boolean isServer;
    private List<NetworkARPGPlayer> players = new ArrayList<NetworkARPGPlayer>();
    //store items that couldn't be registered and register as soon as possible;
    private List<NetworkEntity> leftToRegister = new ArrayList<>();
    private List<NetworkEntity> leftToUnregister = new ArrayList<>();
    private Connection connection;
    private String username;
    private NetworkARPGPlayer player;

    private double time = 0;
    private NetworkArena area;

    public NARPG(boolean isServer, Connection connection) {
        super();
        this.isServer = isServer;
        this.connection = connection;
    }

    @Override
    public String getTitle() {
        return "ZeldIC - Multiplayer";
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            area = new NetworkArena(connection, isServer);
            addArea(area);
            setCurrentArea("custom/Arena", true);
            if (!isServer) {
                ((Client) connection).login();
                username = ((Client) connection).getUsername();
                DiscreteCoordinates spawnCoords = findRandomSpawn();
                player = new NetworkARPGPlayer(area, Orientation.DOWN, spawnCoords, connection, true, ((Client) connection).getMainId(), username, 0);
                new Packet04Chat(username + " has connected").writeData(connection);
                area.registerActor(player);
                area.setViewCandidate(player);
                player.getSpawnPacket().writeData(connection);

                players.add(player);
            }
            return true;
        }
        return false;
    }

    private DiscreteCoordinates findRandomSpawn() {
        boolean canSpawnTo = false;
        DiscreteCoordinates coords;
        NetworkBomb dummy = new NetworkBomb(area, Orientation.DOWN, new DiscreteCoordinates(1, 1), 0);

        do {
            coords = new DiscreteCoordinates(getRandomPos(), getRandomPos());
            canSpawnTo = area.canEnterAreaCells(dummy, Collections.singletonList(coords));
        } while (!canSpawnTo);

        return coords;
    }

    private int getRandomPos() {
        // from 1 to 27
        return RandomGenerator.getInstance().nextInt(26) + 1;
    }

    public void updateObject(Packet03Update update) {
        var entity = findEntity(update.getObjectId());
        if ( entity == null ) return;
        entity.updateState(update.getUpdateMap());
    }

    public void moveObject(Packet02Move packet) {
        for (NetworkEntity p : area.getNetworkEntities()) {
            if (p.isMovable() && packet.getObjectId() == p.getId()) {
                ((MovableNetworkEntity) p).networkMove(packet);
            }
        }
    }

    public void addChat(Packet04Chat packet) {
        area.getAnnouncement().addAnnouncement(packet.getText());
    }

    public boolean spawnObject(Packet00Spawn packet) {
        NetworkEntities object = packet.getObject();
        switch (object) {
            case PLAYER:
                for (NetworkARPGPlayer p : players) {
                    if (p.getId() == packet.getObjectId()) {
                        return false;
                    }
                }
                var newPlayer = new NetworkARPGPlayer(area, packet.getOrientation(),
                        packet.getDiscreteCoordinate(), connection, false, packet.getInitialState());
                newPlayer.setId(packet.getObjectId());
                players.add(newPlayer);
                boolean registered = area.registerActor(newPlayer);
                if (!registered) leftToRegister.add(newPlayer);
                break;
            case BOMB:
                NetworkBomb newBomb = new NetworkBomb(area, packet.getOrientation(), packet.getDiscreteCoordinate(), packet.getInitialState());
                area.registerActor(newBomb);
                break;
            case BOW:
                NetworkArrow newArrow = new NetworkArrow(area, packet.getOrientation(), packet.getDiscreteCoordinate(), connection, packet.getInitialState(), packet.getObjectId());
                area.registerActor(newArrow);
                break;
            case STAFF:
                NetworkMagic newMagic = new NetworkMagic(area, packet.getOrientation(), packet.getDiscreteCoordinate(), connection, packet.getInitialState());
                area.registerActor(newMagic);
        }

        return true;
    }

    public void login() {
        for (NetworkEntity p : area.getNetworkEntities()) {
            Packet00Spawn packet = p.getSpawnPacket();
            packet.writeData(connection);
        }
    }

    //Handles disconnects and player kills
    public void logout(Packet05Logout logoutPacket) {
        for (Iterator<NetworkARPGPlayer> iter = players.listIterator(); iter.hasNext(); ) {
            NetworkARPGPlayer p = iter.next();
            if (p.getId() == logoutPacket.getObjectId()) {
                area.unregisterActor(p);
                iter.remove();
                if (isServer) {
                    if (p.isDead()) {
                        NetworkARPGPlayer killer = (NetworkARPGPlayer) findEntity(p.getKiller());
                        new Packet04Chat(killer.getUsername() + "  killed " + p.getUsername()).writeData(connection);
                        HashMap<String, String> updateMap = new HashMap<>();
                        updateMap.put("killed", String.valueOf(killer.getKills()));
                        new Packet03Update(killer.getId(), updateMap).writeData(connection, killer.getConnectionId());
                    } else {
                        new Packet04Chat(p.getUsername() + " has disconnected").writeData(connection);
                    }
                    logoutPacket.writeData(connection);
                }
            }
        }
    }

    public void despawnEntity(Packet06Despawn packet) {
        NetworkEntity entity = findEntity(packet.getObjectId());
        leftToUnregister.add(entity);
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        // register the entities that still need to be registered
        for (NetworkEntity e : leftToRegister) {
            if (area.registerActor(e)) {
                leftToRegister.remove(e);
                return;
            }
        }
        area.unregisterActor(leftToUnregister);
        leftToUnregister.clear();
        for (NetworkARPGPlayer p : players) {
            if (p.isDead() && p.isClientAuthority()) {
                area.end();
            }
        }

        if (time > 5d) {
            //extremely useful for debugging
            //if(players.size()>0) System.out.println("p1 : " +((NetworkArena)getCurrentArea()).getBehavior().getEntityCount(players.get(0)));
            //if(players.size()>1) System.out.println("p2 : " +((NetworkArena)getCurrentArea()).getBehavior().getEntityCount(players.get(1)));
            for (NetworkARPGPlayer p : players) {
                if (p.getHp() == 0) {
                    area.unregisterActor(p);
                }
            }
            time -= 5d;
        }
        super.update(deltaTime);
    }

    public NetworkEntity findEntity(int objectId) {
        for (NetworkEntity e : area.getNetworkEntities()) {
            if (e.getId() == objectId) return e;
        }
        return null;
    }

    @Override
    public void end() {
        if (player == null || getCurrentArea() == null) {
            return;
        }

        getWindow().dispose();
    }

    public int getClientPlayerId() {
        for (NetworkARPGPlayer p : players) {
            if (p.isClientAuthority()) return p.getId();
        }
        throw new NoSuchElementException("no client player");
    }

    public int getClientPlayerId(long mainId) {
        for (NetworkARPGPlayer p : players) {
            if (p.getConnectionId() == mainId) return p.getId();
        }
        throw new NoSuchElementException("no client player found by mainId");
    }
}
