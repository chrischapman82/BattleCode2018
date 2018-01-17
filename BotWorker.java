import bc.*;

public class BotWorker extends Bot{

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

      public static int PATIENCE = 20;          //basically 4 turns. Patience/num_workers.
      public static int turns_waited = 0;


    public static void update(Unit unit) {

        // TODO: if being attacked. Send a message and stuff
        // TODO: Gathering Karbonite
        int id = unit.id();



        // 1. Checks if should any blueprints can be built nearby. Builds them if yes
        if (tryToBuild(unit)) {
            return;
        }

        if (doRocketStuff(unit)) {
            return;
        }

        // TODO retreat when being attacked

        // 2. If can replicate. Do that.
        if (tryToReplicate(unit)) {
            return;
        }

        // trying out some rocket stuff


        // 3. Checks if I should and can build a building,
        if (tryToCreateBuilding(id, UnitType.Factory)) {
            return;
        }
        if (tryToCreateBuilding(id, UnitType.Rocket)) {
            return;
        }

        // 4. Tries to mine
        if (tryToMine(id)) {
            return;
        }

        // TODO: Find Karbonite
        Nav.wander(id);
        //findKarbonite(unit);
        return;
    }

    // Worker tries to build a building
    // If yes, either builds the building, or looks for a good spot for it
    public static boolean tryToCreateBuilding(int id, UnitType building) {

        // can't build on mars
        if (Globals.planet_name.equals(Planet.Mars)) {
            return false;
        }

        // checks if we need the given building or make one if money is over x amount!
        if (building.equals(UnitType.Factory) && Globals.getNumUnitsOfType(building) >= Globals.getReqUnitsOfType(building)) {

            // if we're floating a lot of money, but have the req number of factories, try to make another
            //if (Player.gc.karbonite() < 250) {
                return false;
            //}

            // for rockets
            // build whenever I've made the rocket available
        } else if (building.equals(UnitType.Rocket) && Player.gc.round() < Research.rocketAvailableRound) { //&& Globals.prev_rockets >= Globals.req_rockets){
            return false;
        }

        // can I afford it
        if (Player.gc.karbonite() < bc.bcUnitTypeBlueprintCost(building)) {
            return false;
        }

        // alright can we blueprint
        if (tryToBlueprintBuilding(id, building)) {
            return true;
        }

        // this means that we should wander off to try and find a good spot
        //TODO find a good factory spot!!! not too close to another factory
        Nav.wander(id);
        return true;
    }


    // Tries to blueprint a certain building
    public static boolean tryToBlueprintBuilding(int id, UnitType building) {

        //System.out.println("Trying to blueprint Factory");
        // TODO making sure that I choose a good factory spot
        // where does not block friendly units, not on minerals

        // check if I have enough money
        if (Player.gc.karbonite() < bc.bcUnitTypeBlueprintCost(building)) {
            return false;
        }

        for (Direction dir : Direction.values()) {
            //TODO: Place on one with least ores?

            //System.out.println("Looking for a place to put the factory");
            if (Player.gc.canBlueprint(id, building, dir)) {

                // checks if the factory is being built on a shitty square
                Direction possible_blocked_dir = Direction.North;
                int num_blocked_dirs = 0;

                // counts the number of unpassable squares next to this location
                // Might only need this with factory
                MapLocation loc = Nav.getMapLocFromId(id);
                for (int i=0; i<Globals.NUM_DIRECTIONS; i++) {
                    possible_blocked_dir = bc.bcDirectionRotateLeft(possible_blocked_dir);
                    if (Tile.isBlocked(loc.add(possible_blocked_dir))) {
                        num_blocked_dirs++;
                    }
                }

                // if a lot of spots are blocked, don't place it there
                if (turns_waited <= PATIENCE && num_blocked_dirs>=5) {
                    turns_waited++;
                    return false;
                }
                //System.out.println("Placing Factory blueprint.");
                if (Player.gc.senseNearbyUnitsByType(loc, 4 ,UnitType.Factory).size() != 0) {
                    turns_waited++;
                    return false;
                }
                Player.gc.blueprint(id, building, dir);
                // adds the loc to our map. means that if a rocket dies, I can't really tell

                Globals.countUnit(building);

                turns_waited = 0;
                return true;
            }
        }
        // if got here, couldn't find a spot to build factory
        //TODO find a spot where I can build a factory

        //Nav.wander(id);
        return false;
    }


    public static void findKarbonite(Unit unit) {


        // I'd really like to do a BFS around me.
    }

    // TODO: Find Karbonite, rather than just wandering w/out purpose
    // Only valid on Earth
    // TODO WTF
    public static void findKarbonite2(Unit unit) {

        //System.out.println(Globals.karboniteMap);
        //System.out.println(Globals.karbonite_left);
        // VERY COMPUTATIONALLY HEAVY!
        // there's nothing left to do
        if (!Globals.karbonite_left) {
            return;
        }

        // there's nothing left!
        if (Globals.karboniteMap.size() == 0) {
            //system.out.println("ALL KARBONITE IS NOW GONE");
            Globals.karbonite_left = false;
        }

        // finds the closest karbonite
        MapLocation unit_loc = unit.location().mapLocation();
        long closest_dist = Integer.MAX_VALUE;
        long curr_dist;
        MapLocation curr_loc;
        MapLocation closest_loc = Globals.karboniteMap.get(0);
        for (int i=0; i<Globals.karboniteMap.size(); i++) {

            curr_loc = Globals.karboniteMap.get(i);
            curr_dist = unit_loc.distanceSquaredTo(curr_loc);

            if (curr_dist < closest_dist) {
                closest_dist = curr_dist;
                closest_loc = curr_loc;
            }
        }

        if (Player.gc.canSenseLocation(closest_loc) && !(Player.gc.karboniteAt(closest_loc) > 0)) {
            Globals.karboniteMap.remove(closest_loc);
            findKarbonite(unit);                        // ewww
        }
        Nav.moveTo(unit.id(), closest_loc);
        return;

        /*MapLocation loc = unit.location().mapLocation();
        // update if there's no karbonite
        int x = loc.getX();
        int y = loc.getY();
        int index = Tile.getIndex(x,y);

        int num_reps=0;
        while (num_reps < 10 && index < Globals.earth_size) {

            //TODO LUL
            index = Tile.increaseX(index);
            if (Globals.initKarboniteSpots.get(index)) {
                if ((Player.gc.karboniteAt(Tile.getMapLocation(index)) == 0)) {
                    Nav.moveTo(unit.id(), loc);
                    break;

                // otherwise update the current map
                } else {
                    Globals.initKarboniteSpots.set(index,false);
                }
            }
            num_reps++;
        }*/

    }


    // Checks if has the money or energy to replicate
    // Then replicates in a random direction if possible
    // TODO change random code so that it doesn't always start North
    public static boolean tryToReplicate(Unit unit) {



        if ((Globals.getNumUnitsOfType(UnitType.Worker) >= Globals.getReqUnitsOfType(UnitType.Worker))
                                        || Player.gc.karbonite() < 15 || unit.abilityHeat() >= 10) {
            if (!(Player.gc.round()>750)) {
                return false;
            }
        }
        int num_tries = 20;
        Direction dir;
        for (int i=0; i<num_tries; i++) {

            dir = Player.getRandomDir();
            if (Player.gc.canReplicate(unit.id(), dir)) {
                Player.gc.replicate(unit.id(), dir);
                Globals.countUnit(UnitType.Worker);
                //Globals.need_workers = false;
                return true;
            }

        }

        return false;
    }

    // tries to build objects next to it.
    // returns true if is building.
    public static boolean tryToBuild(Unit unit) {

        if (Globals.planet_name.equals(Planet.Mars)) {
            return false;
        }
        // checks right next to the player
        VecUnit nearby = Player.gc.senseNearbyUnits(unit.location().mapLocation(), 5);

        // nothing nearby. Exit
        if (nearby.size() == 0) {
            return false;
        }

        // Checks if the things nearby are buildable
        for (int i=0; i<nearby.size(); i++) {
            Unit other = nearby.get(i);

            // Checks if something repairable is next to the worker
            if (Player.gc.canBuild(unit.id(), other.id())) {
                Player.gc.build(unit.id(), other.id());
                //unit_finished = true;
                return true;
            }
        }
        return false;
    }

    // Mines any ore next to the worker
    public static boolean tryToMine(int id) {
        //System.out.println("Trying to mine");
        //checks if any ore next to the worker:
        Direction candidate_dir = Player.getRandomDir();
        for (int i = 0; i < Globals.NUM_DIRECTIONS; i++) {
            candidate_dir = bc.bcDirectionRotateLeft(candidate_dir);
            if (Player.gc.canHarvest(id, candidate_dir)) {
                Player.gc.harvest(id, candidate_dir);
                Globals.karboniteMap.remove(Nav.getMapLocFromId(id).add(candidate_dir));          // remove doesn't error if not in there!
                //System.out.println(Nav.getMapLocFromId(id).add(candidate_dir));
                //System.out.println(Globals.karboniteMap.contains(Nav.getMapLocFromId(id).add(candidate_dir)));
                //System.out.println("Mining...");

                // remove the spot from the map
                //Globals.initKarboniteSpots.set(Tile.getIndex(Nav.getMapLocFromId(id).add(candidate_dir)),false);
                return true;
            }
        }
        return false;
    }
    //TODO: move towards other karbonite rather than just wander


}
