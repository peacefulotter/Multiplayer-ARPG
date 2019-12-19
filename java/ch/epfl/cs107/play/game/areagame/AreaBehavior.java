package ch.epfl.cs107.play.game.areagame;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Image;
import ch.epfl.cs107.play.window.Window;

import java.util.*;


/**
 * AreaBehavior is a basically a map made of Cells. Those cells are used for the game behavior
 */
public abstract class AreaBehavior
{
    /// The behavior is an Image of size height x width
    private final Image behaviorMap;
    private final int width, height;
    /// We will convert the image into an array of cells
    private final Cell[][] cells;

    /**
     * Default AreaBehavior Constructor
     * @param window (Window): graphic context, not null
     * @param name (String): name of the behavior image, not null
     */
    protected AreaBehavior(Window window, String name){
        // Load the image
        behaviorMap = window.getImage(ResourcePath.getBehaviors(name), null, false);
        // Get the corresponding dimension and init the array
        height = behaviorMap.getHeight();
        width = behaviorMap.getWidth();
        cells = new Cell[width][height];
    }

    public List<Vector> getEntityCount(Interactable findObject){
        List<Vector> entitiesList= new ArrayList<>();
        int count=0;
        for(int i = 0; i<width;i++){
            for(int j =0; j<height;j++){
                var entities=cells[i][j].getEntities();
                if(entities.contains(findObject)) entitiesList.add(new Vector(i,j));
            }
        }
        return  entitiesList;
    }
    public void ClearCells(Actor clearObject){
        for(int i = 0; i<width;i++){
            for(int j =0; j<height;j++){
                var entities=cells[i][j].getEntities();
                if(entities.contains(clearObject) && !Objects.equals(clearObject.getPosition(), new Vector(i, j))){
                    cells[i][j].leave((Interactable)clearObject);
                }
            }
        }
    }

    void cellInteractionOf(Interactor interactor){
        for(DiscreteCoordinates dc : interactor.getCurrentCells()){
            if(dc.x < 0 || dc.y < 0 || dc.x >= width || dc.y >= height)
                continue;
            cells[dc.x][dc.y].cellInteractionOf(interactor);
        }
    }


    void viewInteractionOf(Interactor interactor){
        for(DiscreteCoordinates dc : interactor.getFieldOfViewCells()){
            if(dc.x < 0 || dc.y < 0 || dc.x >= width || dc.y >= height)
                continue;
            cells[dc.x][dc.y].viewInteractionOf(interactor);
        }
    }



    boolean canLeave(Interactable entity, List<DiscreteCoordinates> coordinates) {

        for(DiscreteCoordinates c : coordinates){
            if(c.x < 0 || c.y < 0 || c.x >= width || c.y >= height)
                return false;
            if(!cells[c.x][c.y].canLeave())
                return false;
        }
        return true;
    }

    public boolean canEnter(Interactable entity, List<DiscreteCoordinates> coordinates) {
        for(DiscreteCoordinates c : coordinates){
            if(c.x < 0 || c.y < 0 || c.x >= width || c.y >= height)
                return false;
            if(!cells[c.x][c.y].canEnter(entity))
                return false;
        }
        return true;
    }

    //changed it to public as we needed it for NetworkARPGLPlayer
    public void leave(Interactable entity, List<DiscreteCoordinates> coordinates) {

        for(DiscreteCoordinates c : coordinates){
            cells[c.x][c.y].leave(entity);
        }

    }

    void enter(Interactable entity, List<DiscreteCoordinates> coordinates) {
        for(DiscreteCoordinates c : coordinates){
            cells[c.x][c.y].enter(entity);
        }
    }

    protected int getRGB(int r, int c) {
        return behaviorMap.getRGB(r, c);
    }

    protected int getHeight() {
        return height;
    }

    protected int getWidth() {
        return width;
    }

    protected void setCell(int x,int y, Cell cell) {
        cells[x][y] = cell;
    }

    protected Cell getCell(int x, int y) {
        return cells[x][y];
    }

    // Cell as inner class
    public abstract class Cell implements Interactable{

        /// Content of the cell as a set of Interactable
        final Set<Interactable> entities;
        private final DiscreteCoordinates coordinates;


        Set<Interactable> getEntities(){
            return entities;
        }
        /**
         * Default Cell constructor
         * @param x (int): x-coordinate of this cell
         * @param y (int): y-coordinate of this cell
         */
        protected Cell(int x, int y){
            entities = new HashSet<>();
            coordinates = new DiscreteCoordinates(x, y);
        }

        protected boolean hasNonTraversableContent() {
            for (Interactable entity : entities) {
                if (entity.takeCellSpace())
                    return true;
            }
            return false;
        }

        /**
         * Do the given interactor interacts with all Interactable sharing the same cell
         * @param interactor (Interactor), not null
         */
        private void cellInteractionOf(Interactor interactor){ // REFACTOR: must become private with inner class
            interactor.interactWith(this);
            for(Interactable interactable : entities){
                if(interactable.isCellInteractable())
                    interactor.interactWith(interactable);
            }
        }

        /**
         * Do the given interactor interacts with all Interactable sharing the same cell
         * @param interactor (Interactor), not null
         */
        private  void viewInteractionOf(Interactor interactor){
            interactor.interactWith(this);
            for(Interactable interactable : entities){
                if(interactable.isViewInteractable())
                    interactor.interactWith(interactable);
            }
        }

        /**
         * Do the given interactable enter into this Cell
         * @param entity (Interactable), not null
         */
        void enter(Interactable entity) {
            entities.add(entity);
        }

        /**
         * Do the given Interactable leave this Cell
         * @param entity (Interactable), not null
         */
        void leave(Interactable entity) {
            entities.remove(entity);
        }

        /**
         * Indicate if the given Interactable can leave this Cell
         * @return (boolean): true if entity can leave
         */
        protected abstract boolean canLeave();

        /**
         * Indicate if the given Interactable can enter this Cell
         * @param entity (Interactable), not null
         * @return (boolean): true if entity can enter
         */
        protected abstract boolean canEnter(Interactable entity);

        /// Cell implements Interactable

        @Override
        public boolean takeCellSpace(){
            return false;
        }

        @Override
        public List<DiscreteCoordinates> getCurrentCells() {
            return Collections.singletonList(coordinates);
        }

    }
}
