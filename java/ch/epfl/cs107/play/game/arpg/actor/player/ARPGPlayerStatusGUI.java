package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGItem;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import org.jetbrains.annotations.NotNull;

public class ARPGPlayerStatusGUI implements Graphics {
    private float width;
    private float height;
    private Vector anchor;
    private static final float DEPTH=10000f;

    private ImageGraphics gearDisplay;
    private Vector gearDisplayOffset;

    private ImageGraphics itemSprite;
    private TextGraphics itemCount;
    private Vector itemSpriteOffset;
    private float itemSpriteSize = .8f;

    private ARPGPlayer player;


    public ARPGPlayerStatusGUI(@NotNull Canvas canvas, ARPGPlayer player){
        this.player=player;

        width= canvas.getScaledWidth();
        height= canvas.getScaledHeight();
        anchor= canvas.getTransform().getOrigin().sub(new Vector(width/2,height/2));


        gearDisplayOffset=new Vector(0, height-1.75f);
        itemSpriteOffset=new Vector(width/2, 3f);

        gearDisplay= new ImageGraphics(ResourcePath.getSprite("zelda/gearDisplay"),
                1.5f,1.5f, new RegionOfInterest(0,0,32,32), anchor.add(gearDisplayOffset),1f,DEPTH);
        itemSprite= new ImageGraphics(ResourcePath.getSprite(player.getEquippedItem().getSpriteName()),itemSpriteSize,itemSpriteSize,
                new RegionOfInterest(0,0,16,16),anchor.add(itemSpriteOffset),1f,DEPTH-1);
    }
    @Override
    public void draw(Canvas canvas) {
        setItemSprite(player.getEquippedItem());
        width= canvas.getScaledWidth();
        height= canvas.getScaledHeight();
        anchor= canvas.getTransform().getOrigin().sub(new Vector(width/2,height/2));
        gearDisplay.setAnchor(anchor.add(gearDisplayOffset));
        gearDisplay.draw(canvas);
        if(itemSprite!=null){
            itemSprite.setAnchor(anchor.add(gearDisplayOffset));
            itemSprite.setAnchor(gearDisplay.getAnchor().add(.35f,.35f));
            itemSprite.draw(canvas);
        }
    }
    public void setItemSprite(ARPGItem item){
        if(item==null){
            itemSprite=null;
            return;
        }
        itemSprite= new ImageGraphics(ResourcePath.getSprite(item.getSpriteName()),itemSpriteSize,itemSpriteSize,
                new RegionOfInterest(0,0,16,16),anchor.add(itemSpriteOffset),1f,DEPTH);
    }
}
