package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPGBehavior;
import ch.epfl.cs107.play.game.arpg.actor.monster.FlameSkull;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bomb extends AreaEntity implements Interactor {

    private Sprite[] bombSprite;
    private int bombRadius=3;
    private float fuseTime;
    private Animation animation;
    private boolean exploded=false;
    private static final float BOMB_DAMAGE = .5f;
    private final BombHandler handler;

    public Bomb(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area,orientation,position);
        fuseTime=3f;
        bombSprite = new Sprite[2];
        bombSprite[0]= new Sprite("zelda/bomb",1,1f,this, new RegionOfInterest(0,0,16,16),Vector.ZERO,1f,0);
        bombSprite[1]= new Sprite("zelda/bomb",1,1f,this, new RegionOfInterest(16,0,16,16),Vector.ZERO,1f,0);
        Sprite[] animationSprites= new Sprite[7];
        for(int i=0; i<7;i++){
            animationSprites[i] = new Sprite("zelda/explosion", bombRadius,bombRadius,this, new RegionOfInterest(i*32,0,32,32), new Vector(-bombRadius/2,-bombRadius/2));
        }
        animation = new Animation(4,animationSprites, false);
        handler = new BombHandler();
    }

    @Override
    public void draw(Canvas canvas) {
        if(fuseTime>0){
            if(Math.cos(20/(fuseTime))>0){
                bombSprite[0].draw(canvas);
            }else{
                bombSprite[1].draw(canvas);
            }
        }
        else if(!animation.isCompleted())
            animation.draw(canvas);
    }


    public void explode()
    {
        fuseTime = 0;
        exploded = true;
    }

    @Override
    public void update(float deltaTime) {
        fuseTime-=deltaTime;
        if(animation.isCompleted()) {
            getOwnerArea().unregisterActor(this);
        }else if(fuseTime<0){
            animation.update(deltaTime);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return new ArrayList<DiscreteCoordinates>(Collections.singleton(getCurrentMainCellCoordinates()));
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> fieldOfViewCells= new ArrayList<DiscreteCoordinates>();
        DiscreteCoordinates mainCell = getCurrentCells().get(0);
        int offsetFromMainCell= (bombRadius-1)/2;
        for(int i=mainCell.x-offsetFromMainCell; i<mainCell.x+offsetFromMainCell+1;i++){

            for(int j=mainCell.y-offsetFromMainCell; j<mainCell.y+offsetFromMainCell+1;j++){
                if(i>=0 && j>=0){
                   fieldOfViewCells.add(new DiscreteCoordinates(i,j));
                }

            }
        }
        return fieldOfViewCells;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        if(fuseTime<0 && !exploded) {
            exploded=true;
            return true;
        }
        return false;
    }

    @Override
    public void interactWith(Interactable other)
    {
        other.acceptInteraction( handler );
    }


    private class BombHandler implements ARPGInteractionVisitor
    {
        @Override
        public void interactWith( Grass grass )
        {
            grass.cutGrass();
        }

        @Override
        public void interactWith( ARPGPlayer player )
        {
            player.giveDamage( BOMB_DAMAGE );
        }

        public void interactWith( FlameSkull flameSkull )
        {
            explode();
            flameSkull.giveDamage( BOMB_DAMAGE );
        }
    }


}
