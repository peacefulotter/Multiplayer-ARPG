package ch.epfl.cs107.play.game.narpg.actor.player;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.player.ARPGPlayerStatusGUI;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class NetworkARPGPlayerGUI extends ARPGPlayerStatusGUI {
    private static NetworkARPGPlayer owner;
    private static final ImageGraphics upgradedSprite  = new ImageGraphics(
            ResourcePath.getSprite("custom/upgrade"),
            12, 4, new RegionOfInterest(0, 0, 240, 80),
            Vector.ZERO, 1f, 11000 );

    public NetworkARPGPlayerGUI(ARPGPlayer player, String currentItemSpriteName) {
        super(player, currentItemSpriteName);
        owner = (NetworkARPGPlayer)player;
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw( canvas );
        if ( !owner.isShowUpgrades() ) { return; }
        upgradedSprite.setWidth( canvas.getScaledWidth() );
        upgradedSprite.setHeight( canvas.getScaledWidth() / 3 );
        upgradedSprite.setAnchor( getBottomLeftAnchor() );
        upgradedSprite.draw( canvas );
    }
}
