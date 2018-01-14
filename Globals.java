import bc.*;

import java.util.ArrayList;

// stores the global vars accessed by all, reducing clutter in Player
public class Globals {

    public static Team us;
    public static Team them;

    // storing some mapLocations
    public static MapLocation enemy_init_loc;
    public static Planet planet_name;           // PlanetMap planet is much more useful so calling it planet
    public static PlanetMap planet;             // the mpa of the planet that this player is playing on

    public static MapLocation landingLoc;

    public static int req_factories = 2;  // I want one as soon as ossible
    public static int NUM_DIRECTIONS = 8;

    // map dimensions:
    public static int planet_width;
    public static int planet_height;
    public static int planet_size;

    public static int num_workers;
    public static int num_factories;
    public static int num_knights;
    public static int num_rangers;


    // number of each for the previous turn.
    // start high will only last 1 turn. Used to make sure that mass replication doesn't happen first round
    public static int prev_workers = 100;
    public static int prev_factories = 100;
    public static int prev_rockets = 100;
    public static int prev_knights = 100;
    public static int prev_rangers = 100;

    public static int req_workers = 4;
    public static int req_rockets = 0;

    public static boolean need_workers = false;
    public static boolean needFactory = false;


    public static int startBuildingRocketsRound = 500;      // should this be final

    //public static ArrayList<Boolean> karboniteMap;
    public static ArrayList<MapLocation> karboniteMap;
    public static boolean karbonite_left = true;

    public static ArrayList<Unit> priorityEnemies;
    public static boolean timeToGo = true;      // FOR THE ROCKET

    public static void init() {

        // setting up teams
        us = Player.gc.team();
        // I hope there's a better way for this
        if (us.equals(Team.Red)) {
            them = Team.Blue;
        } else {
            them = Team.Red;
        }

        // initialising the planet

        // Setting up the planet values for easy access
        planet_name = Player.gc.planet();
        planet = Player.gc.startingMap(Player.gc.planet());
        planet_height = (int)planet.getHeight();
        planet_width = (int)planet.getWidth();
        planet_size = planet_height*planet_width;

        // init the karbonite map
        karboniteMap = Map.createKarboniteLocMap(planet);
        //System.out.println("*****");
        //System.out.println(planet.initialKarboniteAt(new MapLocation(Planet.Earth,0,8)));

        enemy_init_loc = findInitEnemyLoc();
        priorityEnemies = new ArrayList<>();


        // time to init bfs:

        if (Player.gc.planet() == Planet.Earth) {
            Nav.initNavDirections(enemy_init_loc);
        } else {
            Nav.initNavDirections(new MapLocation(Planet.Mars, 1, 1));
        }

    }

    // finds an initial location to go to.
    public static MapLocation findInitEnemyLoc() {

        VecUnit all_units = planet.getInitial_units();

        // basically for mars
        if (all_units.size() == 0) {
            return null;
        }
        for (int i=0; i< all_units.size(); i++) {
            Unit curr_unit = all_units.get(i);
            if (curr_unit.team().equals(them)) {
                return curr_unit.location().mapLocation();
            }
        }
        // should not come to this.
        return null;
    }

    public static void updateGlobals() {
        resetUnitCounters();
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

    // Not really being used atm
    public static void updateUnitReqs() {
        //System.out.format("prev_workers = %d, req_workers = %d, num_workers = %d\n", prev_workers, req_workers, num_workers);
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

    // prints infromation about the map for debugging purposes
    public static void printMapInfo() {
        // print the width, height etc.
        System.out.format("MAP INFO: width = %d, height = %d\n", planet_width, planet_height);
    }

}
