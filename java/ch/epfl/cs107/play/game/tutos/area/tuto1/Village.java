package ch.epfl.cs107.play.game.tutos.area.tuto1;

import ch.epfl.cs107.play.game.tutos.actor.SimpleGhost;
import ch.epfl.cs107.play.game.tutos.area.SimpleArea;
import ch.epfl.cs107.play.math.Vector;

/**
 * Specific area
 */
public class Village extends SimpleArea {
	
	@Override
	public String getTitle() {
		return "zelda/Village";
	}
	
	@Override
	protected void createArea() {
        // Base
	
        //registerActor(new Background(this)) ;
		registerActor(new SimpleGhost(new Vector(20, 10), "ghost.2"));
        }
	
	
    
}
