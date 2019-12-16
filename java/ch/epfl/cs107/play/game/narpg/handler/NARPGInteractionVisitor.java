package ch.epfl.cs107.play.game.narpg.handler;

import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.narpg.actor.NetworkBomb;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkCoin;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkHeart;

public interface NARPGInteractionVisitor extends ARPGInteractionVisitor
{
    default void interactWith( NetworkARPGPlayer player ) {}
    default void interactWith( NetworkCoin coin ) {}
    default void interactWith( NetworkHeart heart ) {}
    default void interactWith( NetworkBomb bomb ) {}
}
