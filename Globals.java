import bc.*;

// stores the globals, reducing clutter in Player
public class Globals {

    public static Team us;
    public static Team them;

    // storing some mapLocations
    public static MapLocation enemy_init_loc;
    public static PlanetMap earth_init;

    public static int curr_factories = 0;  // starts as 0
    public static int req_factories = 1;  // I want one as soon as ossible

    public static int NUM_DIRECTIONS = 8;
    public static void init(GameController gc) {

        us = gc.team();
        // I hope there's a better way for this
        if (us.equals(Team.Red)) {
            them = Team.Blue;
        } else {
            them = Team.Red;
        }

        earth_init = Player.gc.startingMap(Planet.Earth);
        //System.out.println(earth_init.getInitial_units().toString());
        enemy_init_loc = findInitEnemyLoc();
    }



    // finds an initial location to go to.
    public static MapLocation findInitEnemyLoc() {

        VecUnit all_units = earth_init.getInitial_units();
        for (int i=0; i< all_units.size(); i++) {
            Unit curr_unit = all_units.get(i);
            if (curr_unit.team().equals(them)) {
                return curr_unit.location().mapLocation();
            }
        }
        // should not come to this.
        return null;
    }
}
