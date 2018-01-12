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

    public static void update(Unit unit) {

        int id = unit.id();

       // Nav.wander(id);

        // 1. Checks if any nearby blueprints should be built
        if (tryToBuild(unit)) {
            //System.out.println("Building...");
            return;
        }

        //System.out.println(unit.abilityCooldown() >= 10);
        //System.out.println(unit.abilityCooldown());
        // 2. If can replicate. Do that.
        if (tryToReplicate(unit)) {
            //System.out.println("Replicating...");
            return;
        }

        // 3. Checks if I should and can build a factory,
        if (Globals.prev_factories < Globals.req_factories && Player.gc.karbonite() >= bc.bcUnitTypeBlueprintCost(UnitType.Factory)) {

            if (tryToBlueprintFactory(id)) {
                return;
            } else {
                //TODO find a good factory spot
                Nav.wander(id);
                return;
            }
        }

        if (tryToMine(id)) {
            return;
        }

        // TODO: Find Karbonite
        findKarbonite(id);
        return;
    }

    // TODO: Find Karbonite, rather than just wandering w/out purpose
    // Only valid on Earth
    public static void findKarbonite(int id) {
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

        Nav.wander(id);
    }

    public static boolean tryToBlueprintFactory(int id) {
        // TODO making sure that I choose a good factory spot
        // where does not block friendly units, not on minerals

        // check if I have enough money
        if (Player.gc.karbonite() < bc.bcUnitTypeBlueprintCost(UnitType.Factory)) {
            return false;
        }

        for (Direction dir : Direction.values()) {
            //TODO: Don't place on ores?

            //System.out.println("Looking for a place to put the factory");
            if (Player.gc.canBlueprint(id, UnitType.Factory, dir)) {

                //System.out.println("Placing Factory blueprint.");
                Player.gc.blueprint(id, UnitType.Factory, dir);
                // am I allowed to build straight away?
                //System.out.println("Factory blueprint placed.");
                Globals.prev_factories++;
                return true;
            }
        }
        // if got here, couldn't find a spot to build factory
        //TODO find a spot where I can build a factory

        //Nav.wander(id);
        return false;
    }

    // Checks if has the money or energy to replicate
    // Then replicates in a random direction if possible
    // TODO change random code so that it doesn't always start North
    public static boolean tryToReplicate(Unit unit) {

        if ((Globals.prev_workers < Globals.req_workers) || Player.gc.karbonite() < 15 || unit.abilityHeat() >= 10) {
            return false;
        }
        for (Direction dir : Direction.values()) {
            if (Player.gc.canReplicate(unit.id(), dir)) {
                Player.gc.replicate(unit.id(), dir);
                Globals.prev_workers++;
                //Globals.need_workers = false;
                return true;
            }
        }
        return false;
    }

    // tries to build objects next to it.
    // returns true if is building.
    public static boolean tryToBuild(Unit unit) {

        // checks right next to the player
        VecUnit nearby = Player.gc.senseNearbyUnits(unit.location().mapLocation(), 2);

        // nothing nearby. Exit
        if (nearby.size() == 0) {
            return false;
        }

        // Checks if the things nearby are buildable
        for (int j=0; j<nearby.size(); j++) {
            Unit other = nearby.get(j);

            // Checks if something repairable is next to the worker
            if (Player.gc.canBuild(unit.id(), other.id())) {
                Player.gc.build(unit.id(), other.id());
                //unit_finished = true;
                return true;
            }
        }

        return false;
    }

    public static boolean tryToMine(int id) {

        //System.out.println("Trying to mine");
        //checks if any ore next to the worker:
        Direction candidate_dir = Player.getRandomDir();
        for (int i = 0; i < Globals.NUM_DIRECTIONS; i++) {
            candidate_dir = bc.bcDirectionRotateLeft(candidate_dir);
            if (Player.gc.canHarvest(id, candidate_dir)) {
                Player.gc.harvest(id, candidate_dir);
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
