package ch.epfl.cs107.play.game.narpg.areas;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.arpg.area.ARPGArea;

public class NetworkArena extends ARPGArea
{
    private final Connection connection;
    private final boolean isServer;

    public NetworkArena(Connection connection, boolean isServer )
    {
        this.connection = connection;
        this.isServer = isServer;
    }

    @Override
    protected void createArea()
    {
        // load the background for the client and server
        registerActor( new Background( this ) );
    }

    @Override
    public String getTitle()
    {
        return "custom/Arena";
    }


}
