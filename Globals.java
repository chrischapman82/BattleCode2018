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
    public static PlanetMap earth;


    public static int req_factories = 2;  // I want one as soon as ossible

    public static int NUM_DIRECTIONS = 8;

    // map dimensions:
    public static int earth_width;
    public static int earth_height;
    public static int earth_size;

    public static int num_workers;
    public static int num_factories;
    public static int num_knights;
    public static int num_rangers;

    // number of each for the previous turn.
    // start high will only last 1 turn. Used to make sure that mass replication doesn't happen first round
    public static int prev_workers = 100;
    public static int prev_factories = 100;
    public static int prev_knights = 100;
    public static int prev_rangers = 100;

    public static int req_workers = 4;

    public static boolean need_workers = false;
    public static boolean needFactory = false;

    public static ArrayList<Boolean> initKarboniteSpots;

    public static ArrayList<Unit> priorityEnemies;

    public static void init() {

        us = Player.gc.team();
        // I hope there's a better way for this
        if (us.equals(Team.Red)) {
            them = Team.Blue;
        } else {
            them = Team.Red;
        }

        earth = Player.gc.startingMap(Planet.Earth);
        //earth.initialKarboniteAt();

        earth_height = (int)earth.getHeight();
        earth_width = (int)earth.getWidth();
        earth_size = earth_height*earth_width;

        enemy_init_loc = findInitEnemyLoc();
        priorityEnemies = new ArrayList<>();


        // time to init bfs:
        Nav.initNavDirections(enemy_init_loc);


        // will have O(1) lookup this way.
        /*for (int i=0; i<earth_size; i++) {
            if ((earth.initialKarboniteAt(new MapLocation(Planet.Earth, i%earth_width,i*earth_width))) == 1) {
                initKarboniteSpots.add(true);
            } else {
                initKarboniteSpots.add(false);
            }
        }*/
    }

    // finds an initial location to go to.
    public static MapLocation findInitEnemyLoc() {

        //Player.gc.myUnits().get(0).location().mapLocation();

        VecUnit all_units = earth.getInitial_units();
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

        prev_workers = num_workers;
        prev_factories = num_factories;
        prev_knights = num_knights;
        prev_rangers = num_rangers;

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

        if (prev_workers < req_workers) {
            need_workers = true;
        } else {
            need_workers = false;
        }
    }

    public static boolean addPriorityEnemy(Unit enemy) {
        priorityEnemies.add(enemy);
        return true;
    }

    public static boolean priorityEnemyContains(Unit enemy) {
        if (priorityEnemies.contains(enemy)) {
            return true;
        }
        return false;
    }

    public static boolean killEnemyUnit(Unit enemy) {
        if (priorityEnemies.contains(enemy)) {
            priorityEnemies.remove(enemy);
            return true;
        }
        return false;
    }

}
