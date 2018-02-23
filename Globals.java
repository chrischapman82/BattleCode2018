import bc.*;

import java.util.ArrayList;

// stores the global vars accessed by all, reducing clutter in Player
public class Globals {

    public static Team us;
    public static Team them;

    public static boolean charge;       // true if I just wanna start charging them w/out giving a fuck

    // storing some mapLocations
    public static ArrayList<MapLocation> enemy_init_loc;
    public static MapLocation enemy_loc;

    public static Planet planet_name;           // PlanetMap planet is much more useful so calling it planet
    public static PlanetMap planet;             // the mpa of the planet that this player is playing on

    public static MapLocation landingLoc;

    public static int NUM_DIRECTIONS = 8;

    // map dimensions:
    public static int planet_width;
    public static int planet_height;
    public static int planet_size;

    // TODO change these to a single array, with worker = index 0, fact = index 1 etc.
    public static ArrayList<Integer> curr_units;
    public static ArrayList<Integer> num_units;
    public static ArrayList<Integer> req_units;

    public static final int WORKER_INDEX = 0;
    public static final int RANGER_INDEX = 1;
    public static final int KNIGHT_INDEX_ = 2;
    public static final int FACTORY_INDEX = 3;
    public static final int ROCKET_INDEX = 4;
    public static final int MAGE_INDEX = 5;
    public static final int HEALER_INDEX = 6;

    public static final int NUM_UNIT_TYPES = 7;        // the number of possible units


    // number of each for the previous turn.
    // start high will only last 1 turn. Used to make sure that mass replication doesn't happen first round


    public static boolean makeKnights = false;

    //public static ArrayList<Boolean> karboniteMap;
    public static ArrayList<Integer> karboniteMap;
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

        //System.out.println("*****");
        //System.out.println(planet.initialKarboniteAt(new MapLocation(Planet.Earth,0,8)));

        enemy_init_loc = findInitEnemyLoc();
        priorityEnemies = new ArrayList<>();

        StructRocket.initRocketInfo();

        // setting up the unit stuff
        curr_units = initUnitsArray(curr_units);
        num_units = initUnitsArray(num_units);
        req_units = initUnitsArray(req_units);
        num_units.set(WORKER_INDEX, 100);       // stops mass replication first round
        initReqUnits();     // for strategy


        // time to init bfs:
        if (Player.gc.planet() == Planet.Earth) {
            Nav.initNavDirections(enemy_init_loc);
            Map.init_maps(planet);
        } else {
            //Nav.initNavDirections(new MapLocation(Planet.Mars, 1, 1));
        }
    }

    public static ArrayList<Integer> initUnitsArray(ArrayList<Integer> array) {
        array = new ArrayList<>(NUM_UNIT_TYPES);
        for (int i=0; i<NUM_UNIT_TYPES;i++) {
            array.add(i,0);
        }
        return array;
    }

    public static void initReqUnits() {

        // 4 workers seems good
        req_units.set(WORKER_INDEX, 4);

        // 2 factories seems dope
        req_units.set(FACTORY_INDEX, 2);


        // probably just 4 rockets is enough
        req_units.set(ROCKET_INDEX, 4);

    }

    // finds an initial location to go to.
    public static ArrayList<MapLocation> findInitEnemyLoc() {

        VecUnit all_units = planet.getInitial_units();

        // included so that mars doesn't run
        if (all_units.size() == 0) {
            return null;
        }

        // gets the
        Unit curr_unit;
        MapLocation curr_unit_loc;
        ArrayList<MapLocation> init_locs = new ArrayList<>();
        for (int i=0; i< all_units.size(); i++) {
            curr_unit = all_units.get(i);
            if (curr_unit.team().equals(them)) {
                curr_unit_loc = curr_unit.location().mapLocation();

                // also, don't choose spots that are too close to each other

                //System.out.println();
                if (init_locs.size() >= 1 && curr_unit_loc.distanceSquaredTo(init_locs.get(init_locs.size()-1)) < 10) {
                    //System.out.println(init_locs.get(init_locs.size()-1));
                    //System.out.println(curr_unit_loc);
                    continue;
                }
                init_locs.add(curr_unit_loc);
            }
        }

        // should not come to this.
        if (init_locs.size() == 0) {
            return null;
        } else {
            return init_locs;
        }
    }

    public static void updateGlobals() {

        resetUnitCounters();

        if (Player.gc.round() > 175) {
            makeKnights = true;
        }

        if (Player.gc.round() > Research.rocketAvailableRound - 10) {
            req_units.set(WORKER_INDEX, 20);
        }


    }

    public static void resetUnitCounters() {

        num_units.set(WORKER_INDEX, curr_units.get(WORKER_INDEX));
        num_units.set(RANGER_INDEX, curr_units.get(RANGER_INDEX));
        num_units.set(KNIGHT_INDEX_, curr_units.get(KNIGHT_INDEX_));
        num_units.set(FACTORY_INDEX, curr_units.get(FACTORY_INDEX));
        num_units.set(ROCKET_INDEX, curr_units.get(ROCKET_INDEX));
        num_units.set(MAGE_INDEX, curr_units.get(MAGE_INDEX));
        num_units.set(HEALER_INDEX, curr_units.get(HEALER_INDEX));

        // reset all the counters for the round
        for (int i=0; i<curr_units.size(); i++) {
            curr_units.set(i,0);
        }
    }

    public static void countUnit(UnitType unit_type) {

        switch (unit_type) {
            case Worker:
                curr_units.set(WORKER_INDEX,curr_units.get(WORKER_INDEX)+1);
                break;
            case Knight:
                curr_units.set(KNIGHT_INDEX_,curr_units.get(KNIGHT_INDEX_)+1);
                break;
            case Ranger:
                curr_units.set(RANGER_INDEX,curr_units.get(RANGER_INDEX)+1);
                break;
            case Factory:
                curr_units.set(FACTORY_INDEX,curr_units.get(FACTORY_INDEX)+1);
                break;
            case Rocket:
                curr_units.set(ROCKET_INDEX, curr_units.get(ROCKET_INDEX)+1);
                break;
            case Mage:
                curr_units.set(MAGE_INDEX, curr_units.get(MAGE_INDEX)+1);
                break;
            case Healer:
                curr_units.set(HEALER_INDEX, curr_units.get(HEALER_INDEX)+1);
                break;
            default:
                break;
        }
    }


    // some yummy copy paste code

    public static Integer getNumUnitsOfType(UnitType unit_type) {
        switch (unit_type) {
            case Worker:
                return num_units.get(WORKER_INDEX);
            case Knight:
                return num_units.get(KNIGHT_INDEX_);
            case Ranger:
                return num_units.get(RANGER_INDEX);
            case Factory:
                return num_units.get(FACTORY_INDEX);
            case Rocket:
                return num_units.get(ROCKET_INDEX);
            case Mage:
                return num_units.get(MAGE_INDEX);
            case Healer:
                return num_units.get(HEALER_INDEX);
            default:
                // should never come to this
                return null;
        }
    }

    public static Integer getCurrUnitsOfType(UnitType unit_type) {
        switch (unit_type) {
            case Worker:
                return curr_units.get(WORKER_INDEX);
            case Knight:
                return curr_units.get(KNIGHT_INDEX_);
            case Ranger:
                return curr_units.get(RANGER_INDEX);
            case Factory:
                return curr_units.get(FACTORY_INDEX);
            case Rocket:
                return curr_units.get(ROCKET_INDEX);
            case Mage:
                return curr_units.get(MAGE_INDEX);
            case Healer:
                return curr_units.get(HEALER_INDEX);
            default:
                // should never come to this
                return null;
        }
    }

    public static Integer getReqUnitsOfType(UnitType unit_type) {
        switch (unit_type) {
            case Worker:
                return req_units.get(WORKER_INDEX);
            case Knight:
                return req_units.get(KNIGHT_INDEX_);
            case Ranger:
                return req_units.get(RANGER_INDEX);
            case Factory:
                return req_units.get(FACTORY_INDEX);
            case Rocket:
                return req_units.get(ROCKET_INDEX);
            case Mage:
                return req_units.get(MAGE_INDEX);
            case Healer:
                return req_units.get(HEALER_INDEX);
            default:
                // should never come to this
                return null;
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
        //system.out.format("MAP INFO: width = %d, height = %d\n", planet_width, planet_height);
    }

}
