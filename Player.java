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
        System.out.println(bc.bcDirectionRotateLeft(Direction.North));

        // Connect to the manager, starting the game
        gc = new GameController();

        PlanetMap map = Player.gc.startingMap(Planet.Earth);
        System.out.println("*****");
        System.out.println("Map height and width");
        System.out.println(map);
        System.out.format("%d %d", map.getHeight(), map.getWidth());
        //System.out.println(gc.startingMap(Planet.Earth).getInitial_units().get(0));

        // Initialise globals and Qing research
        Globals.init();
        Research.init();

        System.out.format("Enemy at: %s", Globals.enemy_init_loc.toString());
        System.out.println(Globals.earth.isPassableTerrainAt(new MapLocation(Planet.Earth,0,0)));
        // Direction is a normal java enum.
        Direction[] directions = Direction.values();


        //initialMap = bc.bcPlanetMapFromJson()
        // Getting the teams for both. Used in some of the given methods
        // Seems like a dumb way to do it, but here we are.

        //TODO: Create an array at the start to find all locations w/ kryptonite
        //PlanetMap map = gc.startingMap(Planet.Earth);

        //TODO: Identify choke point



        while (true) {
            //System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();

            // reset at the start of every round
            Globals.resetUnitCounters();

            try {
                for (int i = 0; i < units.size(); i++) {

                    // Defining stuff now
                    Unit unit = units.get(i);
                    System.out.format("%s reporting in\n", unit.unitType());
                    int id = unit.id();
                    Location unit_loc = unit.location();

                    //System.out.println("Unit loc:");
                    //System.out.println(unit_loc);

                    // if the unit is garrisoned or in a rocket. Do nothing as it can't do anything.
                    if (unit_loc.isInGarrison() && !unit.unitType().equals(UnitType.Factory)) {
                        System.out.println("Is garrisoned, do nothing");
                        continue;
                    }

                    // Getting the type of unit and tracking how many
                    UnitType unit_type = unit.unitType();
                    Globals.countUnit(unit_type);

                    // switch was being real dodgy so I guess if statements it is
                    // Going through all the unit types
                    if (unit_type.equals(UnitType.Factory)) {
                        StructFactory.update(unit);

                    } else if (unit_type.equals(UnitType.Worker)) {
                        BotWorker.update(unit);

                    } else if (unit_type.equals(UnitType.Knight)){
                        BotKnight.update(unit);
                    } else {
                        BotRanger.update(unit);
                    }
                }


                // checks if we're going to need more of a type of unit next round
                Globals.updateUnitReqs();

            } catch (Exception e) {
                e.printStackTrace();
            }


            /* TODO: Find a way to check if we have enough factories at a certain point
            if (Globals.num_factories != Globals.req_factories) {

            }*/
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
