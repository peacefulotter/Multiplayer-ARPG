package ch.epfl.cs107.play.game.arpg.handler;

import ch.epfl.cs107.play.game.arpg.ARPGBehavior;
import ch.epfl.cs107.play.game.arpg.actor.monster.FlameSkull;
import ch.epfl.cs107.play.game.arpg.actor.monster.Monster;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.inventory.items.Coin;
import ch.epfl.cs107.play.game.arpg.inventory.items.Heart;
import ch.epfl.cs107.play.game.rpg.handler.RPGInteractionVisitor;

public interface ARPGInteractionVisitor extends RPGInteractionVisitor
{
    default void interactWith( ARPGBehavior.ARPGCell cell ) {}
    default void interactWith( ARPGPlayer player ) {}
    default void interactWith( Coin coin ) {}
    default void interactWith( Heart heart ) {}
    default void interactWith( FlameSkull flameSkull ) {}
    default void interactWith( Monster monster ) {}
}
