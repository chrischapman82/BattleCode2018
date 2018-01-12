import bc.*;

public class Tile {

    public static final int X_INC = 1;          // for incrementing x and y
    public static final int Y_INC = Globals.earth_width;

    public static int getIndex(long x, long y) {
        return (int)(x + y*Globals.earth_width);
    }

    public static int getIndex(MapLocation loc) {
        return (int)(loc.getX()*X_INC + loc.getY()*Y_INC);
    }

    public static int increaseX(int index) {
        return index + X_INC;
    }

    public static int decreaseX(int index) {
        return index-X_INC;
    }

    public static int increaseY(int index) {
        return index+Y_INC;
    }

    public static int decreaseY(int index) {
        return index-Y_INC;
    }

    public static MapLocation getMapLocation(Planet planet, int index) {
        return (new MapLocation(planet, index%Globals.earth_width, index/Globals.earth_width));
    }

    // assuming earth
    public static MapLocation getMapLocation(int index) {
        return (new MapLocation(Planet.Earth, index%Globals.earth_width, index/Globals.earth_width));
    }

}
