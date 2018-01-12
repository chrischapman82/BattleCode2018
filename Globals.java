import bc.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

// stores the globals, reducing clutter in Player
public class Globals {

    public static Team us;
    public static Team them;

    // storing some mapLocations
    public static MapLocation enemy_init_loc;
    public static PlanetMap earth_init;


    public static int req_factories = 2;  // I want one as soon as ossible

    public static int NUM_DIRECTIONS = 8;
    public static int INITIAL_CAPACITY = 11;    // TODO: Should be able to know what the max cap is


    public static int num_workers;
    public static int num_factories;
    public static int num_knights;
    public static int num_rangers;

    public static int req_workers = 5;

    public static boolean need_workers = false;
    public static boolean needFactory = false;

    public static int curr_factories = 0;       //TODO: This should be changed to needFactory to work at some point


    public static void init() {

        us = Player.gc.team();
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

        //Player.gc.myUnits().get(0).location().mapLocation();

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


    public static MapLocation invert(MapLocation location) {
        return null;
    }


    public static void resetUnitCounters() {

        num_workers = 0;
        num_factories = 0;
        num_knights = 0;
        num_rangers = 0;
    }

    public static void countUnit(UnitType unit_type) {

        switch (unit_type) {
            case Factory:
                num_factories++;
                break;
            case Worker:
                num_workers++;
                break;
            case Knight:
                num_knights++;
                break;
            case Ranger:
                num_rangers++;
                break;
            default:
                break;
        }
    }

    public static void updateUnitReqs() {

        if (num_workers < req_workers) {
            need_workers = true;
        } else {
            need_workers = false;
        }
    }

}
