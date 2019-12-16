package ch.epfl.cs107.play.game.narpg.actor;

import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkHeart;
import ch.epfl.cs107.play.game.narpg.actor.projectiles.NetworkArrow;
import ch.epfl.cs107.play.game.narpg.actor.projectiles.NetworkMagic;

public enum NetworkEntities {
    PLAYER(001, NetworkARPGPlayer.class ),
    BOMB(  002, NetworkBomb.class ),
    HEART( 003, NetworkHeart.class ),
    BOW( 004, NetworkArrow.class ),
    STAFF( 005, NetworkMagic.class );

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
