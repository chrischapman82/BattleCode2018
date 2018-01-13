// import the API.
// See xxx for the javadocs.
import bc.*;

import java.util.Random;

public class Player {

    // The class that controls everything!

    // The game controller, basically called for everything. Means that I have to constantly call player but whatever I guess
    public static GameController gc;

    public static void main(String[] args) {

        // Connect to the manager, starting the game
        gc = new GameController();

        // Initialise globals and queuing research
        Globals.init();
        Research.init();

        // checking out whether everything's working!
        Globals.printMapInfo();
        System.out.format("Enemy at: %s", Globals.enemy_init_loc.toString());

        //TODO: Create an array at the start to find all locations w/ kryptonite
        //PlanetMap map = gc.startingMap(Planet.Earth);
        //TODO: Identify choke point

        while (true) {
            System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();

            // try to reset for each round
            Globals.updateGlobals();


                for (int i = 0; i < units.size(); i++) {
                    try {

                    // Storing the unit and other info for later use
                    Unit unit = units.get(i);
                    //System.out.format("%s reporting in\n", unit.unitType());

                    // if the unit is garrisoned or in a rocket. Do nothing as it can't do anything.
                    if (unit.location().isInGarrison() && !unit.unitType().equals(UnitType.Factory)) {
                        //System.out.println("Is garrisoned, do nothing");
                        continue;
                    }

                    // Getting the type of unit and tracking how many
                    UnitType unit_type = unit.unitType();
                    Globals.countUnit(unit_type);

                    // switch was being real dodgy so I guess if statements it is
                    // Changing for each type of unit and delegating to subclasses
                    if (unit_type.equals(UnitType.Factory)) {
                        StructFactory.update(unit);

                    } else if (unit_type.equals(UnitType.Worker)) {
                        BotWorker.update(unit);

                    } else if (unit_type.equals(UnitType.Knight)){
                        BotKnight.update(unit);
                    } else {
                        BotRanger.update(unit);
                    }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Updates the unit requirements, giving a better current state of the units
                Globals.updateUnitReqs();



            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }

    // gets a random direction
    public static Direction getRandomDir() {
        //System.out.println("Getting random direction");
        // fairly sure this isn't random. In fact it's super unlikely to be the last value
        Random rand = new Random(178);
        //System.out.format("rand = %s\n", rand);
        return Direction.values()[rand.nextInt(Globals.NUM_DIRECTIONS)];
    }

    public static void rememberingMethods() {

        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);

        // Getting locs with directions
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
        //System.out.println("loc x: "+loc.getX());

        //System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));
        //System.out.println(bc.bcDirectionRotateLeft(Direction.North));

        System.out.format("%d %d", Globals.earth_width, Globals.earth_width);
    }

}
