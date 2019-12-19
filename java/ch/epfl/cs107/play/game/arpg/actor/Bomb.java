package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.monster.Monster;
import ch.epfl.cs107.play.game.arpg.actor.monster.Vulnerabilities;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bomb extends AreaEntity implements Interactor {

    private Sprite[] bombSprite;
    // the bomb range
    private final static int BOMB_DIAMETER = 3;
    // The time it will take to the bomb to explode
    //is decremented until it reaches 0
    private float fuseTime;
    private Animation animation;
    //exploded needed to make sure explosions only deals damage once while animation plays
    private boolean exploded = false;
    private static final float BOMB_DAMAGE = .5f;
    // type ArpgInteractionVisitor and  not final because its overwritten by NetworkBomb
    protected ARPGInteractionVisitor handler;

    public Bomb(Area area, Orientation orientation, DiscreteCoordinates position) {
        super( area, orientation, position );
        fuseTime = 3f;
        bombSprite = new Sprite[2];
        bombSprite[0]= new Sprite("zelda/bomb",1,1f,this, new RegionOfInterest(0,0,16,16), new Vector( 0, 0.25f ),1f,-100);
        bombSprite[1]= new Sprite("zelda/bomb",1,1f,this, new RegionOfInterest(16,0,16,16), new Vector( 0, 0.25f ),1f,-100);
        Sprite[] animationSprites= new Sprite[7];
        for(int i=0; i<7;i++){
            animationSprites[i] = new Sprite("zelda/explosion", BOMB_DIAMETER, BOMB_DIAMETER,this, new RegionOfInterest(i*32,0,32,32), new Vector(-BOMB_DIAMETER /2,-BOMB_DIAMETER /2));
        }
        animation = new Animation(4,animationSprites, false);
        handler = new BombHandler();
    }

    // getter used by NetworkBomb
    public ARPGInteractionVisitor getHandler()
    {
        return handler;
    }

    @Override
    public void draw(Canvas canvas)
    {
        // draw the bomb if it did not explode
        if (fuseTime > 0) {
            // change between the first and second sprite faster as time goes
            if (Math.cos(20 / (fuseTime)) > 0) {
                bombSprite[0].draw(canvas);
            } else {
                bombSprite[1].draw(canvas);
            }
        } else if (!animation.isCompleted())
        {
            animation.draw(canvas);
        }
    }

    // force the explosion
    public void explode()
    {
        fuseTime = 0;
        exploded = true;
    }

    @Override
    public void update(float deltaTime) {
        // update the fuse time
        fuseTime -= deltaTime;
        // unregister the bomb if the animation is finished
        if (animation.isCompleted()) {
            getOwnerArea().unregisterActor(this);
        // or update the animation
        } else if (fuseTime < 0) {
            animation.update(deltaTime);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList( getCurrentMainCellCoordinates() );
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this);
    }

    /**
     * Get the cells around the bomb by a certain radius
     * This radius can be easily changed to make different kind of bombs :
     *  some more powerful than others
     * @return
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> fieldOfViewCells = new ArrayList<>();
        DiscreteCoordinates mainCell = getCurrentCells().get(0);
        //offsetFromMainCell is how many cells we need to look from the MainCell to get a bomb explosion diameter of bombDiameter
        int offsetFromMainCell = (BOMB_DIAMETER -1) / 2;
        //goes from one side of bomb effect area to the other
        for (int i = mainCell.x - offsetFromMainCell; i < mainCell.x + offsetFromMainCell + 1; i++) {
            //goes from top to bottom of bomb effect area
            for (int j = mainCell.y - offsetFromMainCell; j < mainCell.y + offsetFromMainCell + 1; j++) {
                // ensure the coordinates are in the area
                if ( i >= 0 && j >= 0) {
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

        @Override
        public void interactWith( Monster monster )
        {
            if ( monster.getVulnerabilities().contains( Vulnerabilities.FIRE ) )
            {
                monster.giveDamage( BOMB_DAMAGE );
            }
        }
    }


}
