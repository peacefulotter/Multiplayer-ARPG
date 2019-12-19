package ch.epfl.cs107.play.Networking.utils;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
public enum OrientationValues {
    UP(0, Orientation.UP),
    DOWN(1, Orientation.DOWN),
    LEFT(2, Orientation.LEFT),
    RIGHT(3, Orientation.RIGHT);

    private final int value;
    private final Orientation orientation;

    OrientationValues(int value, Orientation orientation) {
        this.value = value;
        this.orientation = orientation;
    }

    public static int getOrientationValue(Orientation orientation) {
        for (OrientationValues o : OrientationValues.values()) {
            if (orientation == o.getOrientation()) return o.getValue();
        }
        return 1;
    }

    public static Orientation getOrientationByValue(int value) {
        for (OrientationValues o : OrientationValues.values()) {
            if (value == o.getValue()) return o.getOrientation();
        }
        return Orientation.DOWN;
    }

    private Orientation getOrientation() {
        return orientation;
    }

    private int getValue() {
        return value;
    }
}