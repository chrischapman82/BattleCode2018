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
        /*
        ally_team = gc.team();
        if (ally_team.equals(Team.Red)) {
            enemy_team = Team.Blue;
        } else {
            enemy_team = Team.Red;
        }*/


        while (true) {
            //System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();

            try {

                for (int i = 0; i < units.size(); i++) {
                    Unit unit = units.get(i);

                    // if the unit is garrisoned or in a rocket. Wait to be unloaded
                    if (unit.location().isInGarrison()) {
                        continue;
                    }

                    int id = unit.id(); // reducing the amount of times I have to call this silly function

                    // this is actually filthy atm:

                    // getting the type of unit:
                    // At some point I want to separate units into classes
                    switch (unit.unitType()) {
                        case Factory:
                            //if there are any units in the garrison, unload them
                            if ((unit.structureGarrison()).size() > 0 ) {
                                unit.structureGarrison().get(0);

                                //TODO: unload in a random direction so that a blockage won't stop me unloading
                                if (gc.canUnload(id, Direction.North)) {
                                    gc.unload(id, Direction.North);
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
                            VecUnit nearby = gc.senseNearbyUnits(unit.location().mapLocation(), 2);
                            for (int j=0; j<nearby.size(); j++) {
                                Unit other = nearby.get(j);

                                // Checks if something repairable is next to the worker
                                if (gc.canBuild(id, other.id())) {
                                    gc.build(id, other.id());
                                }
                            }

                            // 3. Checks if I should build something
                            if (curr_factories < req_factories) {

                                // Yes I should build a factory some where.
                                // TODO making sure that I choose a good factory spot
                                    // where does not block friendly units, not on minerals

                                for (Direction dir : Direction.values()) {
                                    //TODO: Don't place on ores? Could be an edge case here
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
                            }
                            //TODO: mining. These should


                            //TODO: MOVE
                            break;

                        default:



                            /*  1. If can see an enemy, engage them
                                    If not in atk range, move towards them
                                    If possible, atk enemy
                                2. else just move towards where every one else is going
                             */

                            // by default, if you see an enemy, attack them
                            MapLocation curr_loc = unit.location().mapLocation();
                            VecUnit enemies;

                            // Checks if I can see any enemies
                            if ((enemies = gc.senseNearbyUnitsByTeam(curr_loc, unit.visionRange(), enemy_team))!= null) {

                                // chooses the first enemy arbitrarily. Should be sorted at some point
                                Unit enemy = enemies.get(0);
                                MapLocation enemy_loc = enemy.location().mapLocation();

                                // If not in attack range, move towards the enemy
                                if (curr_loc.distanceSquaredTo(enemy_loc) >= unit.attackRange()) {

                                    Direction enemy_dir = curr_loc.directionTo(enemy_loc);
                                    if (gc.canMove(id, enemy_dir)) {
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

    private static void wander(int id) {
        int NUM_TRIES = 20;
        // just wander I guess. Which sounds pretty shit tbh
        for (int k=0; k<NUM_TRIES; k++) {
            Direction dir = getRandomDir();
            if (gc.canMove(id, dir)) {
                gc.moveRobot(id, dir);
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
        int rand = (int)(Math.random()*(Direction.values().length));
        return Direction.values()[rand];
    }




    // sensing a nearby specific unit
    // senseNearbyUnits

}
