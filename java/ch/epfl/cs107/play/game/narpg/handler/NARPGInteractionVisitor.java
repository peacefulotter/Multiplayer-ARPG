package ch.epfl.cs107.play.game.narpg.handler;

import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.narpg.actor.player.NetworkARPGPlayer;
import ch.epfl.cs107.play.game.narpg.inventory.items.NetworkHeart;

public interface NARPGInteractionVisitor extends ARPGInteractionVisitor
{
    default void interactWith( NetworkARPGPlayer player ) {}
    default void interactWith( NetworkHeart heart ) {}
}
