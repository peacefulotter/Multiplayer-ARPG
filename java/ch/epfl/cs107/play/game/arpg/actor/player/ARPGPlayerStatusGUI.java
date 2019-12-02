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

    private ImageGraphics goldDisplay;
    private Vector goldDisplayOffset;
    private ImageGraphics[] digits;
    private float digitsSize=.8f;

    private ImageGraphics itemSprite;
    private Vector itemSpriteOffset;
    private float itemSpriteSize = .8f;

    private ARPGPlayer player;


    public ARPGPlayerStatusGUI(@NotNull Canvas canvas, ARPGPlayer player) {
        this.player = player;

        width = canvas.getScaledWidth();
        height = canvas.getScaledHeight();
        anchor = canvas.getTransform().getOrigin().sub(new Vector(width / 2, height / 2));


        gearDisplayOffset = new Vector(0, height - 1.5f);
        itemSpriteOffset = new Vector(0.35f, 0.35f);
        goldDisplayOffset = new Vector(0, 0);

        gearDisplay = new ImageGraphics(ResourcePath.getSprite("zelda/gearDisplay"),
                1.5f, 1.5f, new RegionOfInterest(0, 0, 32, 32), anchor.add(gearDisplayOffset), 1f, DEPTH);
        itemSprite = new ImageGraphics(ResourcePath.getSprite(player.getEquippedItem().getSpriteName()), itemSpriteSize, itemSpriteSize,
                new RegionOfInterest(0, 0, 16, 16), anchor.add(itemSpriteOffset), 1f, DEPTH - 1);

        goldDisplay = new ImageGraphics(ResourcePath.getSprite("zelda/coinsDisplay"),
                3, 1.5f, new RegionOfInterest(0, 0, 64, 32), anchor.add(goldDisplayOffset), 1f, DEPTH);
        digits = new ImageGraphics[10];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if ((i + j + 2) > 10) break;
                digits[i+j]=new ImageGraphics(ResourcePath.getSprite("zelda/digits"),
                        digitsSize,digitsSize,new RegionOfInterest(j*16,i*16,16,16),
                        anchor, 1,DEPTH+1);
            }
        }
    }
    @Override
    public void draw(Canvas canvas) {
        setItemSprite(player.getEquippedItem());
        width= canvas.getScaledWidth();
        height= canvas.getScaledHeight();
        anchor= canvas.getTransform().getOrigin().sub(new Vector(width/2,height/2));
        gearDisplay.setAnchor(anchor.add(gearDisplayOffset));
        gearDisplay.draw(canvas);
        goldDisplay.setAnchor(anchor.add(goldDisplayOffset));
        goldDisplay.draw(canvas);
        if(itemSprite!=null){
            itemSprite.setAnchor(anchor.add(gearDisplayOffset));
            itemSprite.setAnchor(gearDisplay.getAnchor().add(itemSpriteOffset));
            itemSprite.draw(canvas);
        }
        digits[2].draw(canvas);
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
