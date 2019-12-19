package ch.epfl.cs107.play.game.arpg.actor.player;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class ARPGPlayerStatusGUI implements Graphics
{
    private Vector bottomLeftAnchor;

    // the gui must be on top whatever is drawn
    private static final float DEPTH = 10000f;

    // the plate behing the equipped item
    private final ImageGraphics gearDisplay;
    // equipped item
    private ImageGraphics itemDisplay;
    private final ImageGraphics moneyBackgroundDisplay;
    private final ImageGraphics[] moneyDigitsDisplay;

    private final float gearDisplaySize = 1.5f;
    private final float moneyBackgroundDisplayWidth = 3f;
    private final float moneyBackgroundDisplayHeight = 1.5f;
    private final float digitsSize = .6f;

    private final ARPGPlayer player;

    public ARPGPlayerStatusGUI( ARPGPlayer player, String currentItemSpriteName )
    {
        this.player = player;

        gearDisplay = new ImageGraphics(
                ResourcePath.getSprite("zelda/gearDisplay"),
                gearDisplaySize, gearDisplaySize, new RegionOfInterest(0,0,32,32),
                Vector.ZERO,1f, DEPTH );


        moneyBackgroundDisplay = new ImageGraphics(
                ResourcePath.getSprite("zelda/coinsDisplay"),
                moneyBackgroundDisplayWidth, moneyBackgroundDisplayHeight, new RegionOfInterest(0, 0, 64, 32),
                Vector.ZERO, 1f, DEPTH );

        // get all the digits in an array
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
        // initialise the item sprite
        setItemSprite( currentItemSpriteName );
    }


    // used in NetworkARPGPlayerGUI
    protected Vector getBottomLeftAnchor()
    {
        return bottomLeftAnchor;
    }

    @Override
    public void draw( Canvas canvas )
    {
        // get the canvas width and height
        float width = canvas.getScaledWidth();
        float height = canvas.getScaledHeight();
        // anchors used to place the images
        Vector topLeftAnchor = canvas.getTransform().getOrigin().sub(new Vector(width / 2, (-height / 2) + gearDisplaySize));
        bottomLeftAnchor = canvas.getTransform().getOrigin().sub( new Vector(width / 2, height / 2 ) );
        // set the item sprite to the equipped item
        setItemSprite(player.getEquippedItem().getSpriteName());

        // set the anchor and draw the gear display at the top left corner
        gearDisplay.setAnchor(topLeftAnchor);
        gearDisplay.draw( canvas );

        // set the anchor and draw the money background at the bottom left corner
        moneyBackgroundDisplay.setAnchor( bottomLeftAnchor );
        moneyBackgroundDisplay.draw( canvas );

        // if the equipped item is not null
        if ( itemDisplay != null ) {
            // centers the itemDisplay inside the gearDisplay
            itemDisplay.setAnchor( gearDisplay.getAnchor().add( .35f, .32f ) );
            itemDisplay.draw( canvas );
        }

        // finally draw the coins (digits) and hearts
        drawCoinCount( canvas );
        drawHearts( canvas );
    }

    /**
     * Draw the digits matching the player money
     * @param canvas
     */
    private void drawCoinCount( Canvas canvas )
    {
        int playerMoney = player.getMoney();
        int len = Integer.toString( playerMoney ).length();

        for( int i = 0; i < len; i++ )
        {
            // get each digit of the player money
            int digitIndex = ( playerMoney % 10 ) - 1;
            if ( digitIndex == -1 ) { digitIndex = 9; }

            // place the coins inside the Money Container
            moneyDigitsDisplay[ digitIndex ].setAnchor(
                    moneyBackgroundDisplay.getAnchor().add(
                            ( digitsSize * 2 / 3 ) * ( 5.7f - i ), moneyBackgroundDisplayHeight / 3 ) ); // hard tweaking
            moneyDigitsDisplay[ digitIndex ].draw( canvas );
            // get to the next power of 10
            playerMoney /= 10;
        }
    }

    /**
     * Draw hearts matching player health
     * @param canvas
     */
    private void drawHearts( Canvas canvas ){
        float hp = player.getHp();
        int hearts = player.getMaxHP();
        ImageGraphics[] heartsDisplay = new ImageGraphics[hearts];

        // for every hearts
        for( int i = 0; i < hearts; i++ )
        {
            // get the sprite offset -> to have a full heart, a half-heart or an empty heart
            int spriteOffset=0;
            if( hp >= 1 ) {
                spriteOffset = 32;
                hp--;
            } else if ( hp==0.5 ) {
                spriteOffset = 16;
                hp -= 0.5f;
            }
            // and add the corresponding heart to the hearts display array
            heartsDisplay[ i ] = new ImageGraphics(
                    ResourcePath.getSprite("zelda/heartDisplay"),
                    1, 1, new RegionOfInterest(spriteOffset,0,16,16),
                    bottomLeftAnchor,  1, DEPTH);
            // set the anchor to align them and space them
            heartsDisplay[ i ].setAnchor(
                    bottomLeftAnchor.add(
                            moneyBackgroundDisplayWidth + i + 0.25f, moneyBackgroundDisplayHeight / 6 ) );
            heartsDisplay[ i ].draw(canvas);

        }
    }

    /**
     * Set the item sprite to the equipped item
     * @param spriteName : the sprite name of the equipped item
     */
    public void setItemSprite( String spriteName )
    {
        float itemSpriteSize = .8f;
        itemDisplay = new ImageGraphics(
                ResourcePath.getSprite( spriteName ),
                itemSpriteSize, itemSpriteSize, new RegionOfInterest(0,0,16,16),
                Vector.ZERO,1f,DEPTH );
    }

}
