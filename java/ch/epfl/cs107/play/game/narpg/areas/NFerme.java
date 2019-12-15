package ch.epfl.cs107.play.game.narpg.areas;

import ch.epfl.cs107.play.Networking.Connection;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.arpg.area.ARPGArea;
import ch.epfl.cs107.play.game.narpg.actor.monster.NetworkLogMonster;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkCoin;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class NFerme extends ARPGArea
{
    private final Connection connection;
    private final boolean isServer;

    public NFerme( Connection connection, boolean isServer )
    {
        this.connection = connection;
        this.isServer = isServer;
    }

    @Override
    protected void createArea()
    {
        // load the background and foreground for the client and server
        registerActor( new Background( this ) );
        registerActor( new Foreground( this ) );
        // but only load entities for the server, then the server tells the clients whats up with those u know
        registerActor( new NetworkLogMonster( this, new DiscreteCoordinates( 8, 8 ), connection, isServer ) );
        registerActor( new NetworkCoin( this, new DiscreteCoordinates( 9, 11 ), 50 ) );
    }

    @Override
    public String getTitle()
    {
        return "zelda/Ferme";
    }
}
