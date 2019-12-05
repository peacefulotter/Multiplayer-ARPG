package ch.epfl.cs107.play.game.arpg.actor.monster;

public enum Vulnerabilities
{
    FIRE( "Fire", true ),
    WATER( "Water", true ),
    MAGIC( "Magic", true ),
    CLOSE_RANGE( "Close Range", false ),
    LONG_RANGE( "Long Range", false );

    private final String name;
    private final boolean isType;

    Vulnerabilities( String name, boolean isType )
    {
        this.name = name;
        this.isType = isType;
    }

}
