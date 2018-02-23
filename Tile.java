import bc.*;

public class Tile {

    public static final int X_INC = 1;          // for incrementing x and y
    public static final int Y_INC = Globals.planet_width;

    public static int getIndex(MapLocation loc) {
        return (int)(loc.getX()*X_INC + loc.getY()*Y_INC);
    }

    public static MapLocation getMapLocation(int index) {
        return (new MapLocation(Globals.planet_name, index%Globals.planet_width, index/Globals.planet_width));
    }

    /*  checks if the given mapLocation is blocked. Blocked if:
     *  1. Not on the map
     *  2. Not passable
     *  3. Has a building there
     */

    public static boolean isBlocked(MapLocation loc) {

        // is it on the map or passable
        if (!Globals.planet.onMap(loc) ||
                Globals.planet.isPassableTerrainAt(loc) == 0
                ) {
            return true;
        }

        // is a structure
        if (Player.gc.hasUnitAtLocation(loc)) {
            UnitType unit_type = Player.gc.senseUnitAtLocation(loc).unitType();
            if (unit_type.equals(UnitType.Factory) || unit_type.equals(UnitType.Rocket)) {
                return true;
            }
        }
        return false;
    }
    // assuming planet
    /*
    public static MapLocation getMapLocation(int index) {
        return (new MapLocation(planet, index%Globals.planet_width, index/Globals.planet_width));
    }*/

}
