package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGItem;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;


public class ARPGPlayerStatusGUI implements Graphics {
    private static final float DEPTH = 10000f;

    private ImageGraphics gearDisplay;

    private ImageGraphics itemSprite;
    private TextGraphics itemCount;
    private final float itemSpriteSize = .8f;
    private final float gearDisplaySize = 1.5f;

    private ARPGPlayer player;


    public ARPGPlayerStatusGUI( Canvas canvas, ARPGPlayer player){
        this.player = player;
        /*gearDisplayOffset=new Vector(0, height-1.75f);
        itemSpriteOffset=new Vector(width/2, 3f);

        gearDisplay= new ImageGraphics(ResourcePath.getSprite("zelda/gearDisplay"),
                1.5f,1.5f, new RegionOfInterest(0,0,32,32), anchor.add(gearDisplayOffset),1f,DEPTH);
        itemSprite= new ImageGraphics(ResourcePath.getSprite(player.getEquippedItem().getSpriteName()),itemSpriteSize,itemSpriteSize,
                new RegionOfInterest(0,0,16,16),anchor.add(itemSpriteOffset),1f,DEPTH-1);
        */
        gearDisplay = new ImageGraphics(
                ResourcePath.getSprite("zelda/gearDisplay"),
                gearDisplaySize, gearDisplaySize, new RegionOfInterest(0,0,32,32),
                Vector.ZERO,1f, DEPTH );
        itemSprite = new ImageGraphics(
                ResourcePath.getSprite( player.getEquippedItem().getSpriteName() ),
                itemSpriteSize, itemSpriteSize, new RegionOfInterest(0,0,32,32),
                Vector.ZERO, 1f, DEPTH-1 );
    }

    @Override
    public void draw( Canvas canvas )
    {
        setItemSprite(player.getEquippedItem());

        float width = canvas.getScaledWidth();
        float height = canvas.getScaledHeight();
        Vector anchor = canvas.getTransform().getOrigin().sub( new Vector( width / 2, ( -height / 2 ) + gearDisplaySize ) );

        gearDisplay.setAnchor( anchor );
        gearDisplay.draw( canvas );

        if ( itemSprite != null ) {
            itemSprite.setAnchor( anchor );
            itemSprite.setAnchor( gearDisplay.getAnchor().add( .35f, .32f ) );
            itemSprite.draw( canvas );
        }
    }

    public void setItemSprite(ARPGItem item){
        if(item==null){
            itemSprite=null;
            return;
        }
        itemSprite= new ImageGraphics(ResourcePath.getSprite(item.getSpriteName()),itemSpriteSize,itemSpriteSize,
                new RegionOfInterest(0,0,16,16),Vector.ZERO,1f,DEPTH);
    }
}
