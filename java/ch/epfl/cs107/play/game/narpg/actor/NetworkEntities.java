package ch.epfl.cs107.play.game.narpg.actor;

import ch.epfl.cs107.play.game.narpg.actor.monster.NetworkLogMonster;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;

public enum NetworkEntities {
    PLAYER(001, NetworkARPGPlayer.class ),
    BOMB(002, NetworkBomb.class ),
    GRASS( 003, NetworkGrass.class ),
    LOG_MONSTER( 004, NetworkLogMonster.class );

    private final int classId;
    private final Class<?> value;

    NetworkEntities( int classId, Class value )
    {
        this.classId = classId;
        this.value = value;
    }

    public int getClassId() {
        return classId;
    }

    public Class<?> getValue() { return value;}

    public static NetworkEntities lookUpEntity(int classId){
        for(NetworkEntities e : NetworkEntities.values()){
            if(e.classId==classId){
                return e;
            }
        }
        return null;
    }

    public static int getValue;
}
