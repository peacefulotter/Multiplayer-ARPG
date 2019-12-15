package ch.epfl.cs107.play.game.narpg.handler;

import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkCoin;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkHeart;
import ch.epfl.cs107.play.game.rpg.handler.RPGInteractionVisitor;

public interface NARPGInteractionVisitor extends RPGInteractionVisitor
{
    default void interactWith( NetworkARPGPlayer player ) {}
    default void interactWith( NetworkCoin coin ) {}
    default void interactWith( NetworkHeart heart ) {}
}
