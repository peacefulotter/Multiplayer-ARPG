package ch.epfl.cs107.play;

import ch.epfl.cs107.play.game.narpg.NARPG;
import ch.epfl.cs107.play.io.DefaultFileSystem;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.io.ResourceFileSystem;
import ch.epfl.cs107.play.io.XMLTexts;
import ch.epfl.cs107.play.window.Window;
import ch.epfl.cs107.play.window.swing.SwingWindow;

public class ThreadedPlay implements Runnable {


    /**
     * One second in nano second
     */
    private static final float ONE_SEC = 1E9f;


    private final FileSystem fileSystem;
    private final NARPG game;
    private final Window window;
    private boolean isServer;
    private boolean stopGame;

    public ThreadedPlay(NARPG game, boolean isServer) {
        this.isServer=isServer;
        // Define cascading file system
        fileSystem = new ResourceFileSystem(DefaultFileSystem.INSTANCE);
        // Create a demo game and initialize corresponding texts
        XMLTexts.initialize(fileSystem, "strings/icmon_fr.xml");
        this.game=game;
        // get the screen size so that the window is matching the display
        // Use Swing displayGame
        boolean testing = Boolean.parseBoolean(System.getProperty("testing"));
        boolean headless=false;
        if(testing || !isServer){
            System.setProperty("java.awt.headless","false");
        }else if(isServer){
            System.setProperty("java.awt.headless","true");
            headless=true;
        }
        window = new SwingWindow(game.getTitle(), fileSystem, 550, 550,headless);
        //Recorder recorder = new Recorder(window);
        //RecordReplayer replayer = new RecordReplayer(window); // not used in this project
    }



    @Override
    public void run() {

        try {

            if (game.begin(window, fileSystem)) {
                //recorder.start();
                //replayer.start("record1.xml");

                // Use system clock to keep track of time progression
                long currentTime = System.nanoTime();
                long lastTime;
                final float frameDuration = ONE_SEC / game.getFrameRate();

                // Run until the user try to close the window
                while (!window.isCloseRequested() && !stopGame) {

                    // Compute time interval
                    lastTime = currentTime;
                    currentTime = System.nanoTime();
                    float deltaTime = (currentTime - lastTime);

                    try {
                        int timeDiff = Math.max(0, (int) (frameDuration - deltaTime));
                        Thread.sleep((int) (timeDiff / 1E6), (int) (timeDiff % 1E6));
                    } catch (InterruptedException e) {
                        System.out.println("Thread sleep interrupted");
                    }

                    currentTime = System.nanoTime();
                    deltaTime = (currentTime - lastTime) / ONE_SEC;

                    // Let the game do its stuff
                    game.update(deltaTime);

                    // Render and update input
                    window.update();
                    //recorder.update();
                    //replayer.update();
                }
            }
            //recorder.stop("record1.xml");
            game.end();

        } finally {
            // Release resources
            System.out.println("closing");
            window.dispose();
        }
    }
}

