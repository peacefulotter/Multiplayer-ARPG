package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class ARPGPlayerStatusGUI implements Graphics {
    private Vector topLeftAnchor;
    private Vector bottomLeftAnchor;

    private static final float DEPTH = 10000f;

    private ImageGraphics gearDisplay;
    private ImageGraphics itemDisplay;
    private ImageGraphics moneyBackgroundDisplay;
    private ImageGraphics[] moneyDigitsDisplay;
    private ImageGraphics[] heartsDisplay;

    private final float itemSpriteSize = .8f;
    private final float gearDisplaySize = 1.5f;
    private final float moneyBackgroundDisplayWidth = 3f;
    private final float moneyBackgroundDisplayHeight = 1.5f;
    private final float heartsDisplaySize = 1.6f;
    private float digitsSize = .6f;

    private ARPGPlayer player;


    public ARPGPlayerStatusGUI( ARPGPlayer player, String currentItemSpriteName ) {
        this.player = player;

        gearDisplay = new ImageGraphics(
                ResourcePath.getSprite("zelda/gearDisplay"),
                gearDisplaySize, gearDisplaySize, new RegionOfInterest(0,0,32,32),
                Vector.ZERO,1f, DEPTH );


        moneyBackgroundDisplay = new ImageGraphics(
                ResourcePath.getSprite("zelda/coinsDisplay"),
                moneyBackgroundDisplayWidth, moneyBackgroundDisplayHeight, new RegionOfInterest(0, 0, 64, 32),
                Vector.ZERO, 1f, DEPTH );

        moneyDigitsDisplay = new ImageGraphics[10];
        for ( int i = 0; i < 3; i++ ) {
            for ( int j = 0; j < 4; j++ ) {
                if ( (i * 4 + j ) >= 10 ) break;
                moneyDigitsDisplay[ i * 4 + j ] = new ImageGraphics(
                        ResourcePath.getSprite("zelda/digits"),
                        digitsSize, digitsSize, new RegionOfInterest(j*16,i*16,16,16),
                        Vector.ZERO, 1,DEPTH+1 );
            }
        }

        setItemSprite( currentItemSpriteName );
    }

    @Override
    public void draw( Canvas canvas )
    {
        float width = canvas.getScaledWidth();
        float height = canvas.getScaledHeight();
        topLeftAnchor = canvas.getTransform().getOrigin().sub( new Vector(width / 2, (-height / 2) + gearDisplaySize ) );
        bottomLeftAnchor = canvas.getTransform().getOrigin().sub( new Vector(width / 2, height / 2 ) );

        gearDisplay.setAnchor( topLeftAnchor );
        gearDisplay.draw( canvas );

        moneyBackgroundDisplay.setAnchor( bottomLeftAnchor );
        moneyBackgroundDisplay.draw( canvas );

        if ( itemDisplay != null ) {
            // centers the itemDisplay inside the gearDisplay
            itemDisplay.setAnchor( gearDisplay.getAnchor().add( .35f, .32f ) );
            itemDisplay.draw( canvas );
        }

        drawCoinCount( canvas );
        drawHearts( canvas );
    }

    private void drawCoinCount( Canvas canvas )
    {
        int playerMoney = player.getMoney();
        int len = Integer.toString( playerMoney ).length();

        for( int i = 0; i < len; i++ )
        {
            int digitIndex = ( playerMoney % 10 ) - 1;
            if ( digitIndex == -1 ) { digitIndex = 9; }

            // place the coins inside the Money Container
            moneyDigitsDisplay[ digitIndex ].setAnchor(
                    moneyBackgroundDisplay.getAnchor().add(
                            ( digitsSize * 2 / 3 ) * ( 5.7f - i ), moneyBackgroundDisplayHeight / 3 ) );
            moneyDigitsDisplay[ digitIndex ].draw( canvas );

            playerMoney /= 10;
        }

    }
    private void drawHearts( Canvas canvas ){
        float hp = player.getHp();
        int hearts = player.getMaxHP();
        heartsDisplay = new ImageGraphics[ hearts ];

        for( int i = 0; i < hearts; i++ )
        {
            int spriteOffset=0;
            if( hp >= 1 ) {
                spriteOffset = 32;
                hp--;
            } else if ( hp==0.5 ) {
                spriteOffset = 16;
                hp -= 0.5f;
            }
            heartsDisplay[ i ] = new ImageGraphics(
                    ResourcePath.getSprite("zelda/heartDisplay"),
                    1, 1, new RegionOfInterest(spriteOffset,0,16,16),
                    bottomLeftAnchor,  1, DEPTH);
            heartsDisplay[ i ].setAnchor(
                    bottomLeftAnchor.add(
                            moneyBackgroundDisplayWidth + i + 0.25f, moneyBackgroundDisplayHeight / 6 ) );
            heartsDisplay[i].draw(canvas);

        }
    }

    public void setItemSprite( String spriteName )
    {
        itemDisplay = new ImageGraphics(
                ResourcePath.getSprite( spriteName ),
                itemSpriteSize,itemSpriteSize, new RegionOfInterest(0,0,16,16),
                Vector.ZERO,1f,DEPTH );
    }

}
