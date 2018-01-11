// import the API.
// See xxx for the javadocs.
import bc.*;

public class Player {

    // I really like the gc being easily accessible
    // public static GameController gc;

    public static GameController gc;
    // storing some mapLocations
    public static MapLocation loc0;

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

        System.out.println("*****");
        //System.out.println(gc.startingMap(Planet.Earth).getInitial_units().get(0));

        // Initialise globals
        Globals.init(gc);

        System.out.format("Enemy at: %s", Globals.enemy_init_loc.toString());
        // Direction is a normal java enum.
        Direction[] directions = Direction.values();

        //initialMap = bc.bcPlanetMapFromJson()
        // Getting the teams for both. Used in some of the given methods
        // Seems like a dumb way to do it, but here we are.

        //TODO: Create an array at the start to find all locations w/ kryptonite
        //PlanetMap map = gc.startingMap(Planet.Earth);


        while (true) {
            //System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();

            try {
                for (int i = 0; i < units.size(); i++) {

                    // Defining stuff now
                    Unit unit = units.get(i);
                    System.out.format("%s reporting in\n", unit.unitType());
                    int id = unit.id();
                    Location unit_loc = unit.location();


                    // if the unit is garrisoned or in a rocket. Do nothing.
                    if (unit_loc.isInGarrison() && unit.unitType()!=UnitType.Factory) {
                        continue;
                    }

                    MapLocation unit_maploc = unit_loc.mapLocation();
                    // this is actually filthy atm:

                    // getting the type of unit:
                    // At some point I want to separate units into classes
                    switch (unit.unitType()) {
                        case Factory:
                            //if there are any units in the garrison, unload them
                            if ((unit.structureGarrison()).size() > 0 ) {
                                System.out.println("I have garrisoned units");
                                unit.structureGarrison().get(0);

                                // unloads in a random direction
                                //TODO: Prioritise towards the enemy
                                //TODO: Should check all exits

                                Direction candidate_dir = getRandomDir();
                                for (int j = 0; j < Globals.NUM_DIRECTIONS; j++) {
                                    candidate_dir = bc.bcDirectionRotateLeft(candidate_dir);
                                    if (gc.canUnload(id, candidate_dir)) {
                                        gc.unload(id, candidate_dir);
                                        System.out.println("unloading...");
                                        break;
                                    }
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
                            //((BotWorker)unit).update();
                            BotWorker.update(unit);

                            break;

                        default:

                            /*  1. If can see an enemy, engage them
                                    If not in atk range, move towards them
                                    If possible, atk enemy
                                2. else just move towards where every one else is going
                             */
                            //wander(id);
                            // by default, if you see an enemy, attack them
                            MapLocation curr_loc = unit.location().mapLocation();
                            VecUnit enemies;

                            BotKnight.update(unit);

                            }
                            break;
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
    // builds a factory. Returns true if a factory was built, else returns false.

    // gets a random direction
    public static Direction getRandomDir() {
        System.out.println("Getting random direction");
        // fairly sure this isn't random. In fact it's super unlikely to be the last value
        int rand = (int)(Math.random()*Direction.values().length);
        System.out.format("rand = %d, len = %d\n", rand, Direction.values().length);
        System.out.println();
        return Direction.values()[rand];
    }
}
