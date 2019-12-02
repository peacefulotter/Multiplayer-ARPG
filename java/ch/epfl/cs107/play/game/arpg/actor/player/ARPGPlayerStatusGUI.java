package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.arpg.inventory.ARPGItem;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Image;

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
    private float digitsSize=.6f;

    private ImageGraphics[] heartsDisplay;
    private Vector heartsDisplayOffset;

    private ImageGraphics itemSprite;
    //private TextGraphics itemCount;
    private final float itemSpriteSize = .8f;
    private final float gearDisplaySize = 1.5f;

    private ARPGPlayer player;


    public ARPGPlayerStatusGUI(Canvas canvas, ARPGPlayer player) {
        this.player = player;
        /* ALBASTARDOTO VERSION
        width = canvas.getScaledWidth();
        height = canvas.getScaledHeight();
        anchor = canvas.getTransform().getOrigin().sub(new Vector(width / 2, height / 2));


        gearDisplayOffset = new Vector(0, height - 1.5f);
        itemSpriteOffset = new Vector(0.35f, 0.35f);
        goldDisplayOffset = new Vector(0, 0);
        heartsDisplayOffset= new Vector(1.5f,0);

        gearDisplay = new ImageGraphics(ResourcePath.getSprite("zelda/gearDisplay"),
                1.5f, 1.5f, new RegionOfInterest(0, 0, 32, 32), anchor.add(gearDisplayOffset), 1f, DEPTH);
        itemSprite = new ImageGraphics(ResourcePath.getSprite(player.getEquippedItem().getSpriteName()), itemSpriteSize, itemSpriteSize,
                new RegionOfInterest(0, 0, 16, 16), anchor.add(itemSpriteOffset), 1f, DEPTH - 1);
        */
        /* PEACEFULOTTER VERSION
        gearDisplay = new ImageGraphics(
                ResourcePath.getSprite("zelda/gearDisplay"),
                gearDisplaySize, gearDisplaySize, new RegionOfInterest(0,0,32,32),
                Vector.ZERO,1f, DEPTH );
        itemSprite = new ImageGraphics(
                ResourcePath.getSprite( player.getEquippedItem().getSpriteName() ),
                itemSpriteSize, itemSpriteSize, new RegionOfInterest(0,0,32,32),
                Vector.ZERO, 1f, DEPTH-1 );
         */
        //GOLD DISPLAY
        /*
        goldDisplay = new ImageGraphics(ResourcePath.getSprite("zelda/coinsDisplay"),
                3, 1.5f, new RegionOfInterest(0, 0, 64, 32), anchor.add(goldDisplayOffset), 1f, DEPTH);
        digits = new ImageGraphics[10];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if ((i*4 + j) >= 10) break;
                digits[i*4+j]=new ImageGraphics(ResourcePath.getSprite("zelda/digits"),
                        digitsSize,digitsSize,new RegionOfInterest(j*16,i*16,16,16),
                        anchor, 1,DEPTH+1);
            }
        }
        //HEARTS DISPLAY*/
    }

    @Override
    public void draw( Canvas canvas )
    {
        setItemSprite(player.getEquippedItem());
        /* PEACEFULOTTER VERSION
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
        */
        /* ALBASTARDOTO VERSION
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
        drawCoinCount(canvas);
        drawHearts(canvas);
        */
        
    }

    private void drawCoinCount(Canvas canvas){
        Vector offsetVector= new Vector(2.2f,.5f);
        int coins=player.getMoney();
        int len= Integer.toString(coins).length();
        ImageGraphics[] digitImageArray= new ImageGraphics[len];
        for(int i=0; i<len;i++){
            int digitIndex=coins%10;
            digitIndex-=1;
            if(digitIndex==-1) digitIndex=9;
            digitImageArray[i]=digits[digitIndex];
            coins/=10;
        }
        for(int i=0; i<len; i++){
           digitImageArray[i].setAnchor(goldDisplay.getAnchor().add(offsetVector).add((-digitsSize/2 - .1f)*i,0));
        }
        for(ImageGraphics digit : digitImageArray){
            digit.draw(canvas);
        }

    }
    private void drawHearts(Canvas canvas){
        int hearts= player.getMaxHP();
        heartsDisplay= new ImageGraphics[hearts];
        float hp=player.getHp();
        for(int i=0; i<hearts; i++){
            int spriteOffset=0;
            if(hp>=1){
                spriteOffset=32;
                hp--;
            }else if(hp==0.5){
                spriteOffset=16;
                hp-=0.5f;
            }
            heartsDisplay[i]=new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"),
                    1,1,new RegionOfInterest(spriteOffset,0,16,16),anchor.add(gearDisplayOffset).add(heartsDisplayOffset).add(0.8f*i,+.3f),
                    1,DEPTH);
            heartsDisplay[i].draw(canvas);
        }
    }

    public void setItemSprite(ARPGItem item){
        if(item==null){
            itemSprite=null;
            return;
        }
        /* ALBASTARDOTO VERSION
        itemSprite= new ImageGraphics(ResourcePath.getSprite(item.getSpriteName()),itemSpriteSize,itemSpriteSize,
                new RegionOfInterest(0,0,16,16),anchor.add(itemSpriteOffset),1f,DEPTH);

        */
        /* PEACEFULOTTER VERSION
        itemSprite= new ImageGraphics(ResourcePath.getSprite(item.getSpriteName()),itemSpriteSize,itemSpriteSize,
                new RegionOfInterest(0,0,16,16),Vector.ZERO,1f,DEPTH);

         */

    }

}
