package ch.epfl.cs107.play.game.arpg.handler;

import ch.epfl.cs107.play.game.arpg.ARPGBehavior;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.CastleDoor;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.monster.*;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.arpg.inventory.items.CastleKey;
import ch.epfl.cs107.play.game.arpg.inventory.items.Coin;
import ch.epfl.cs107.play.game.arpg.inventory.items.CollectibleAreaEntity;
import ch.epfl.cs107.play.game.arpg.inventory.items.Heart;
import ch.epfl.cs107.play.game.rpg.handler.RPGInteractionVisitor;

public interface ARPGInteractionVisitor extends RPGInteractionVisitor
{
    default void interactWith( ARPGBehavior.ARPGCell cell ) {}
    default void interactWith( ARPGPlayer player ) {}
    default void interactWith( CastleDoor door ) {}

    default void interactWith( Coin coin ) {}
    default void interactWith( Heart heart ) {}
    default void interactWith( CastleKey key ) {}
    default void interactWith( Bomb bomb ){}

    default void interactWith( Monster monster ) {}
    default void interactWith( FlameSkull flameSkull ) {}
    default void interactWith( LogMonster logMonster ) {}
    default void interactWith( DarkLord darkLord ) {}
    default void interactWith(FireSpell fireSpell ) {}

    default void interactWith( Arrow arrow ) {}
}
