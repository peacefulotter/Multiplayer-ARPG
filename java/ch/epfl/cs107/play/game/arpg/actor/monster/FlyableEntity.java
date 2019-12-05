package ch.epfl.cs107.play.game.arpg.actor.monster;


import ch.epfl.cs107.play.game.areagame.actor.Interactable;

public interface FlyableEntity extends Interactable
{
    default boolean canFly() { return true; }
}
