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
    private List<TextGraphics> announcements = new ArrayList<>();
    private List<Integer> removeQueue = new ArrayList<>();

    public void addAnnouncement( String text )
    {
        TextGraphics graphics = new TextGraphics( text, 0.4f, Color.GRAY, Color.BLACK, 0.05f, false, false, Vector.ZERO, TextAlign.Horizontal.RIGHT, TextAlign.Vertical.MIDDLE, 0.9f, 1000 );
        announcements.add( graphics );
    }

    @Override
    public void update( float deltaTime )
    {
        int announcementLen = announcements.size();
        for ( int i = 0; i < announcementLen; i++ )
        {
            TextGraphics t = announcements.get( i );
            t.setAlpha( t.getAlpha() - deltaTime / 25 );
            if ( t.getAlpha() <= 0.7 )
            {
                removeQueue.add( i );
            }
        }
        for ( int i : removeQueue )
        {
            announcements.remove( i );
        }
        removeQueue.clear();
    }

    @Override
    public void draw(Canvas canvas)
    {
        int announcementLen = announcements.size();
        for ( int i = 0; i < announcementLen; i++ )
        {
            TextGraphics t = announcements.get( i );
            t.setAnchor( canvas.getTransform().getOrigin().add( canvas.getScaledWidth() / 2 - 0.9f, ( canvas.getScaledHeight() - i - 1 ) / 2 ) );
            t.draw( canvas );
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
