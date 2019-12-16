package ch.epfl.cs107.play.game.narpg.announcement;

import ch.epfl.cs107.play.Networking.NetworkEntity;
import ch.epfl.cs107.play.Networking.Packets.Packet00Spawn;
import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.math.TextAlign;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerAnnouncement implements Graphics, NetworkEntity
{
    private String text;
    private List<TextGraphics> announcements = new ArrayList<>();
    private List<Integer> removeQueue = new ArrayList<>();

    public ServerAnnouncement( String text )
    {
        TextGraphics graphics = new TextGraphics( text, 0.5f, Color.GRAY, Color.BLACK, 0.1f, false, false, new Vector( 0, 1f ), TextAlign.Horizontal.CENTER, TextAlign.Vertical.MIDDLE, 1, 1000 );
        announcements.add( graphics );
        this.text = text;
    }

    @Override
    public void draw(Canvas canvas)
    {
        int announcementLen = announcements.size();
        for ( int i = 0; i < announcementLen; i++ )
        {
            TextGraphics t = announcements.get( i );
            t.setAnchor( canvas.getVelocity() );
            //t.setAnchor( canvas.getTransform().getOrigin().sub( new Vector(canvas.getWidth() / 2, ( canvas.getHeight() / 2 ) + i + 3 ) ) );
            t.draw( canvas );
            t.setAlpha( t.getAlpha() - 1 );
            if ( t.getAlpha() <= 0 )
            {
                removeQueue.add( i );
            }
        }
        purgeAnnouncements();
    }

    private void purgeAnnouncements()
    {
        for ( Integer i : removeQueue )
        {
            announcements.remove( i );
        }
    }

    @Override
    public int getId()
    {
        return 0;
    }

    @Override
    public Packet00Spawn getSpawnPacket()
    {
        return null;
    }

    @Override
    public void updateState(HashMap<String, String> updateMap)
    {

    }

    @Override
    public Transform getTransform()
    {
        return null;
    }

    @Override
    public Vector getVelocity()
    {
        return null;
    }

}
