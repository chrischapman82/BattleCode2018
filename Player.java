// import the API.
// See xxx for the javadocs.
import bc.*;

public class Player {

    public Player() {
       //gc = new GameController();

       // research tab
       //gc.queueResearch(UnitType.Worker);
       //gc.queueResearch(UnitType.Ranger);
       //gc.queueResearch(UnitType.Knight);
       //gc.queueResearch(UnitType.Knight);
       //gc.queueResearch(UnitType.Knight);
       //gc.queueResearch(UnitType.Rocket);

    }
    // I really like the gc being easily accessible
    // public static GameController gc;
    public static Team ally_team;
    public static Team enemy_team;

    public static GameController gc;
    // storing some mapLocations
    public static MapLocation loc0;

    public static int curr_factories = 0;  // starts as 0
    public static int req_factories = 1;  // I want one as soon as ossible

    //Researching

    public static void main(String[] args) {

        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
        System.out.println("loc x: "+loc.getX());

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));

        // Connect to the manager, starting the game
        gc = new GameController();

        // Direction is a normal java enum.
        Direction[] directions = Direction.values();

        // Getting the teams for both. Used in some of the given methods
        // Seems like a dumb way to do it, but here we are.

        ally_team = gc.team();
        if (ally_team.equals(Team.Red)) {
            enemy_team = Team.Blue;
        } else {
            enemy_team = Team.Red;
        }

        //TODO: Create an array at the start to find all locations w/ kryptonite
        //PlanetMap map = gc.startingMap(Planet.Earth);

        //TODO: getting init location of enemy
        // Use: bc_PlanetMap_initial_units_get


        while (true) {
            //System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();

            try {
                for (int i = 0; i < units.size(); i++) {

                    // Defining stuff now
                    Unit unit = units.get(i);
                    int id = unit.id();
                    Location unit_loc = unit.location();


                    // if the unit is garrisoned or in a rocket. Do nothing.
                    if (unit_loc.isInGarrison() && unit.unitType()!=UnitType.Factory) {
                        continue;
                    }

                    MapLocation unit_maploc = unit_loc.mapLocation();
                    boolean unit_finished = false;
                    // this is actually filthy atm:

                    // getting the type of unit:
                    // At some point I want to separate units into classes
                    switch (unit.unitType()) {
                        case Factory:
                            System.out.println("I am a factory");
                            //if there are any units in the garrison, unload them
                            if ((unit.structureGarrison()).size() > 0 ) {
                                System.out.println("I have garrisoned units");
                                unit.structureGarrison().get(0);

                                // unloads in a random direction
                                //TODO: Prioritise towards the enemy
                                //TODO: Should check all exits
                                Direction dir = getRandomDir();
                                if (gc.canUnload(id, dir)) {
                                    gc.unload(id, dir);
                                    System.out.println("Unloading units");
                                }
                            }

                            //Produce a unit if it's possible
                            //Have to check if a factory can unload a unit and build a unit in the same turn
                            if ((gc.canProduceRobot(id, UnitType.Knight))) {
                                gc.produceRobot(id, UnitType.Knight);
                                System.out.println("Producing Knight");
                            }
                            break;

                        // case for the worker
                        case Worker:
                            System.out.println("I am a worker");

                            /*
                                1. If can replicate. Do that. Break.
                                2. If can repair/build next to it. Do that. Break.
                                    Sense    if something repairable is next ot the worker
                                3. If should build something, do that.
                                   Is there a suitable location next to me.
                                    Am I legally allowed to build there
                                Will I block off some units if I build it here?
                                Yes: Do it. No: Move to somewhere close.
                                If can mine. Min
                            */

                            // If can replicate. Do that.
                            replicate(unit, gc);

                            // 2.If can repair/build right next to me, do that

                            VecUnit nearby;

                            // such a filthy way to do this
                            // Checks if there's anything nearby that it can build. If it builds a factory, exits the loop
                            if ((gc.senseNearbyUnits(unit.location().mapLocation(), 2)).size() > 0
                                                                && buildNearbyStructs(unit)) {
                                // means that it doesn't move away
                                continue;
                            }

                            // 3. Checks if I should build a factory
                            if (curr_factories < req_factories){
                                // 3. Checks if I should build something

                                // Yes I should build a factory some where.
                                // TODO making sure that I choose a good factory spot
                                    // where does not block friendly units, not on minerals

                                for (Direction dir : Direction.values()) {
                                    //TODO: Don't place on ores?
                                    System.out.println("Looking for a place to put the factory");
                                    if (gc.canBlueprint(id, UnitType.Factory, dir)) {

                                        System.out.println("Placing Factory blueprint.");
                                        gc.blueprint(id, UnitType.Factory, dir);
                                        // am I allowed to build straight away?
                                        System.out.println("Factory blueprint placed.");
                                        curr_factories++;
                                        break;
                                    }

                                }
                                // if got here, couldn't find a spot to build factory
                                //TODO find a spot where I can build a factory

                                wander(id);
                            } else {
                                // For some good ol harvesting

                                goMine(id, unit_maploc);
                                //wander(id);
                            }

                            break;

                        default:
                            System.out.println("I am a soldier");

                            /*  1. If can see an enemy, engage them
                                    If not in atk range, move towards them
                                    If possible, atk enemy
                                2. else just move towards where every one else is going
                             */
                            //wander(id);
                            // by default, if you see an enemy, attack them
                            MapLocation curr_loc = unit.location().mapLocation();
                            VecUnit enemies;

                            // Checks if I can see any enemies
                            //System.out.println(gc.senseNearbyUnitsByTeam(curr_loc, unit.visionRange(), enemy_team).toString());
                            if ((enemies = gc.senseNearbyUnitsByTeam(curr_loc, unit.visionRange(), enemy_team)).size() > 0) {
                                System.out.println("I have spotted an enemy");
                                // chooses the first enemy arbitrarily. Should be sorted at some point
                                Unit enemy = enemies.get(0);
                                MapLocation enemy_loc = enemy.location().mapLocation();

                                // If not in attack range, move towards the enemy
                                if (curr_loc.distanceSquaredTo(enemy_loc) >= unit.attackRange()) {

                                    Direction enemy_dir = curr_loc.directionTo(enemy_loc);
                                    if (gc.isMoveReady(id) && gc.canMove(id, enemy_dir) ) {
                                        gc.moveRobot(id, enemy_dir);
                                    }
                                }
                                // If I can attack an enemy, do that
                                if (gc.isAttackReady(id) && gc.canAttack(id, enemy.id())) {
                                    // can I attack them?
                                    gc.attack(id, enemy.id());
                                }
                            } else {
                                wander(id);

                            }
                            break;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }

    // builds a factory. Returns true if a factory was built, else returns false.
    private static boolean buildNearbyStructs(Unit unit) {

        VecUnit nearby = gc.senseNearbyUnits(unit.location().mapLocation(), 2);
        for (int j=0; j<nearby.size(); j++) {
            Unit other = nearby.get(j);

            // Checks if something repairable is next to the worker
            if (gc.canBuild(unit.id(), other.id())) {
                gc.build(unit.id(), other.id());
                //unit_finished = true;
                return true;
            }
        }

        return false;
    }

    private static void goMine(int id, MapLocation unit_maploc) {

        boolean has_harvested = false;
        VecMapLocation possible_locs = gc.allLocationsWithin(unit_maploc,2 );
        for (int j=0; j<possible_locs.size(); j++) {
            Direction candidate_dir = unit_maploc.directionTo(possible_locs.get(j));
            if (gc.canHarvest(id, candidate_dir)) {
                gc.harvest(id, candidate_dir);
                has_harvested = true;
                break;
            }
        }

        //TODO: move towards other karbonite rather than just wander
        if (!has_harvested) {
            // fix
            wander(id);
        }
    }


    //TODO: Wander goes in a certain order. This is bad.
    // SHould just be random
    private static void wander(int id) {

        System.out.println("Attempting to wander");
        if (gc.isMoveReady(id)) {
            //dont think is move ready is working
            //if (gc.isMoveReady(id)) {
            int NUM_TRIES = 10;
            // just wander I guess. Which sounds pretty shit tbh
            System.out.println("I am allowed to wander");

            Direction[] dirs = Direction.values();
            for (int k=0; k<Direction.values().length; k++) {
                Direction dir = dirs[k];
                // System.out.format("%s\n", dir);
                if (gc.canMove(id, dir)) {
                    gc.moveRobot(id, dir);
                    System.out.println("Wandering");
                    break;
                }
            }
        }

    }


    // Checks if has the money or energy to replicate
    // Then replicates in a random direction if possible
    // TODO change random code so that it doesn't always start North
    private static void replicate(Unit unit, GameController gc) {

        if (gc.karbonite() < 15 || unit.abilityCooldown() >= 10) {
            return;
        }
        for (Direction dir : Direction.values()) {
            if (gc.canReplicate(unit.id(), dir)) {
                gc.replicate(unit.id(), dir);
                System.out.println("I am replicating myself");
                return;
            }
        }

    }

    // gets a random direction
    public static Direction getRandomDir() {
        // fairly sure this isn't random. In fact it's super unlikely to be the last value
        int rand = (int)(Math.random()*Direction.values().length);
        System.out.format("rand = %d, len = %d\n", rand, Direction.values().length);
        return Direction.values()[rand];
    }




    // sensing a nearby specific unit
    // senseNearbyUnits

}
