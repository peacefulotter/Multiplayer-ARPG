package ch.epfl.cs107.play.game.arpg.actor.monster;


import ch.epfl.cs107.play.game.areagame.actor.Interactable;

/**
 *  A FlyableEntity is a MovableEntity that can go on cells that are not necessarily wallkable
 */
public interface FlyableEntity extends Interactable
{
    default boolean canFly() { return true; }
}
