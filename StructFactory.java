import bc.*;

public class StructFactory extends Structure{

    public static void update(Unit factory) {

        releaseGarrisonUnits(factory);

        // should always produce units. No point in not!

        /*
        if (produceRobot(factory.id(), UnitType.Ranger)) {
            Globals.num_rangers++;
            return;
        }
        */

        // code to do different for each


        /*
        if (factory.team() == Team.Blue) {

            if (produceRobot(factory.id(), UnitType.Knight)) {
                return;
            }
        } else {
            if (produceRobot(factory.id(), UnitType.Ranger)) {
                return;
            }
        }*/
    }

    /*
    public static boolean releaseGarrisonUnits(Unit factory) {

        // checks if there are units inside
        if (factory.structureGarrison().size() == 0) {
            return false;
        }

        int factory_id = factory.id();

        Direction candidate_dir;
        // shitty fix so that it's facing towards the enemy as the first option
        // != for null
        if (!Nav.directions0explored) {
            candidate_dir = bc.bcDirectionRotateRight(Nav.dirToMapLoc(factory, Globals.enemy_init_loc.get(0)));
        } else {
            candidate_dir = Player.getRandomDir();
        }

        // releases the garrisoned unit if a direction is available
        Unit friend;
        for (int i=0; i<Globals.NUM_DIRECTIONS; i++) {
            candidate_dir = bc.bcDirectionRotateLeft(candidate_dir);

            if (Player.gc.canUnload(factory_id, candidate_dir)) {
                Player.gc.unload(factory_id, candidate_dir);
                return true;
            }

            // if there's a friendly unit there. Tell them to move
            MapLocation candidate_loc = Nav.getMapLocFromId(factory_id).add(candidate_dir);

            if (Player.gc.hasUnitAtLocation(candidate_loc) &&
                    (friend = Player.gc.senseUnitAtLocation(candidate_loc)).team() == factory.team()) {
                if (Nav.politelyAskToMove(friend, candidate_dir) && Player.gc.canUnload(factory_id, candidate_dir)) {
                    Player.gc.unload(factory_id, candidate_dir);
                    //Nav.moveToEnemyBase(Player.gc.senseUnitAtLocation(Nav.getMapLocFromId(factory_id).add(candidate_dir)).id());
                    return true;
                }

            }
        }

        return false;

    }*/

    //Produce a unit if it's possible
    //Have to check if a factory can unload a unit and build a unit in the same turn
    public static boolean produceRobot(int factory_id, UnitType unit_type) {

        // TODO: Create workers if we run out

        if ((Player.gc.canProduceRobot(factory_id, unit_type))) {

            Player.gc.produceRobot(factory_id, unit_type);
            System.out.format("Producing %s\n", unit_type);
            // TODO count these?
            return true;
        }

        return false;

    }
}
