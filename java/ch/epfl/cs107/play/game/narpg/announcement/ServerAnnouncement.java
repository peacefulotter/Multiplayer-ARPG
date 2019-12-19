package ch.epfl.cs107.play.game.narpg.announcement;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.math.TextAlign;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ServerAnnouncement implements Actor {
    private final List<TextGraphics> announcements = new ArrayList<>();
    private final List<Integer> removeQueue = new ArrayList<>();

    public void addAnnouncement(String text) {
        TextGraphics graphics = new TextGraphics(text, 0.6f, Color.GRAY, Color.BLACK, 0.05f, true, false, Vector.ZERO, TextAlign.Horizontal.RIGHT, TextAlign.Vertical.MIDDLE, 1f, 1000);
        announcements.add(graphics);
    }

    @Override
    public void update(float deltaTime) {
        int announcementLen = announcements.size();
        for (int i = 0; i < announcementLen; i++) {
            TextGraphics t = announcements.get(i);
            t.setAlpha(t.getAlpha() - deltaTime / 15);
            if (t.getAlpha() <= 0.6) {
                removeQueue.add(i);
            }
        }
        for (int i : removeQueue) {
            announcements.remove(i);
        }
        removeQueue.clear();
    }

    @Override
    public void draw(Canvas canvas) {
        int announcementLen = announcements.size();
        for (int i = 0; i < announcementLen; i++) {
            TextGraphics t = announcements.get(i);
            t.setAnchor(canvas.getTransform().getOrigin().add(canvas.getScaledWidth() / 2 - 0.6f, (canvas.getScaledHeight() - i*1.3f - 1) / 2));
            t.draw(canvas);
        }
    }


    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return null;
    }
}
