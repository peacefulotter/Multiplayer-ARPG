package ch.epfl.cs107.play.recorder.recordEntry;

import java.awt.Robot;

import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Window;

public class MouseMoveRecordEntry extends RecordEntry{
	private static final long serialVersionUID = 1;
	private final float x;
	private final float y;
	
	public MouseMoveRecordEntry(long time, float x, float y) {
		super(time);
		this.x = x;
		this.y = y;
	}

	@Override
	public void replay(Robot robot, Window window) {
		Vector mousePosition = window.convertPositionOnScreen(new Vector(x,y));
		robot.mouseMove((int)mousePosition.x, (int)mousePosition.y);
	}
}
