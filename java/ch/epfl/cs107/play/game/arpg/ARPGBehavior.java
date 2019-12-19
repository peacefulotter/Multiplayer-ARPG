package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.monster.FlyableEntity;
import ch.epfl.cs107.play.window.Window;

public class ARPGBehavior extends AreaBehavior
{
    public enum ARPGCellType
    {
        //https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
        NULL( 0, false, false ),
        WALL( -16777216, false, false ),
        IMPASSABLE( -8750470, false, true ),
        INTERACT( -256, true, true ),
        DOOR( -195580, true, true ),
        WALKABLE( -1, true, true );

        final int type;
        final boolean isWalkable;
        final boolean isFlyable;

        ARPGCellType( int type, boolean isWalkable, boolean isFlyable )
        {
            this.type = type;
            this.isWalkable = isWalkable;
            this.isFlyable = isFlyable;
        }

        static ARPGBehavior.ARPGCellType toType(int type)
        {
            for ( ARPGBehavior.ARPGCellType ict : ARPGBehavior.ARPGCellType.values() )
            {
                if ( ict.type == type )
                    return ict;
            }
            // When you add a new color, you can print the int value here before assign it to a type
            return NULL;
        }
    }

    public ARPGBehavior( Window window, String name )
    {
        super( window, name );
        int height = getHeight();
        int width = getWidth();
        for ( int y = 0; y < height; ++y )
        {
            for ( int x = 0; x < width; x++ )
            {
                ARPGBehavior.ARPGCellType cellType = ARPGBehavior.ARPGCellType.toType( getRGB( height - 1 - y, x ) );
                setCell( x, y, new ARPGBehavior.ARPGCell( x, y, cellType ) );
            }
        }
    }


    public class ARPGCell extends Cell
    {
        private final ARPGCellType type;

        /**
         * Default Cell constructor
         *
         * @param x (int): x-coordinate of this cell
         * @param y (int): y-coordinate of this cell
         */
        private ARPGCell( int x, int y, ARPGCellType type )
        {
            super( x, y );
            this.type = type;
        }

        public boolean isDoor()
        {
            return type == ARPGCellType.DOOR;
        }

        @Override
        protected boolean canLeave()
        {
            return true;
        }

        @Override
        protected boolean canEnter( Interactable entity )
        {
            if ( entity instanceof FlyableEntity )
            {
                return type.isWalkable || type.isFlyable;
            }
            if ( entity.takeCellSpace() && hasNonTraversableContent() )
            {
                return false;
            }

            return type.isWalkable;
        }

        @Override
        public boolean isCellInteractable()
        {
            return true;
        }

        @Override
        public boolean isViewInteractable()
        {
            return false;
        }

        @Override
        public void acceptInteraction( AreaInteractionVisitor v )
        {

        }
    }
}
