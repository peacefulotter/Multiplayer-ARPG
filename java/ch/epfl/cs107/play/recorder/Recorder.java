package ch.epfl.cs107.play.recorder;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.recorder.recordEntry.KeyboardPressedRecordEntry;
import ch.epfl.cs107.play.recorder.recordEntry.KeyboardReleasedRecordEntry;
import ch.epfl.cs107.play.recorder.recordEntry.MouseButtonPressedRecordEntry;
import ch.epfl.cs107.play.recorder.recordEntry.MouseButtonReleasedRecordEntry;
import ch.epfl.cs107.play.recorder.recordEntry.MouseMoveRecordEntry;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Mouse;
import ch.epfl.cs107.play.window.Window;

class Recorder{
	private final Keyboard keyboard;
	private final Mouse mouse;
	private long startTime;
	private Record record;
	private Vector lastMousePosition;

    public static final String RECORD_DIRECTORY = "records";
	
	public Recorder(Window window) {
		this.keyboard = window.getKeyboard();
		this.mouse = window.getMouse();
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
		record = new Record();
		long randomSeed = RandomGenerator.getInstance().nextLong();
		RandomGenerator.getInstance().setSeed(randomSeed);
		record.setRandomSeed(randomSeed);
	}
	
	public void stop(String filename) throws IllegalArgumentException {
		if(filename == null) throw new IllegalArgumentException();
		try{
			File directory = new File(RECORD_DIRECTORY);
			if(!directory.exists()) directory.mkdirs();

	        File file = new File(directory, filename);
	        file.createNewFile();
	        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
	        out.writeObject(record);
	        out.close();
		}catch(Exception e){
			System.out.println("ERROR: An error happened while saving record");
			e.printStackTrace();
		}
	}
	
	public void update() {
		if(record == null) return;
		
		long time = System.currentTimeMillis() - startTime;

        int KEYBOARD_MAX_KEYCODE = KeyEvent.KEY_LAST;
        for(int key = 0; key <= KEYBOARD_MAX_KEYCODE; ++key) {
			Button button = keyboard.get(key);
			
			if(button.isPressed())
				record.addEntry(new KeyboardPressedRecordEntry(time, key));
			if(button.isReleased())
				record.addEntry(new KeyboardReleasedRecordEntry(time, key));
		}
        int MOUSE_BUTTON_MAX_KEYCODE = 2;
        for(int key = 0; key <= MOUSE_BUTTON_MAX_KEYCODE; ++key) {
			Button button = mouse.getButton(key);			
			if(button.isPressed())
				record.addEntry(new MouseButtonPressedRecordEntry(time, key));
			if(button.isReleased())
				record.addEntry(new MouseButtonReleasedRecordEntry(time, key));
		}
		final Vector mousePosition = mouse.getPosition();
		if(!mousePosition.equals(lastMousePosition)) {
			lastMousePosition = mousePosition;
			record.addEntry(new MouseMoveRecordEntry(time, mousePosition.x, mousePosition.y));
		}
	}
}
