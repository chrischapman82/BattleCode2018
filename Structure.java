import bc.*;

public class Structure extends Unit {
    /*  Contains generic methods for structures.
        ie. Rockets and Factories!
     */

    public static final int MAX_GARRISON_UNITS = 8;


    public static boolean releaseGarrisonUnits(Unit building) {

        // checks if there are units inside
        if (building.structureGarrison().size() == 0) {
            return false;
        }

        int building_id = building.id();

        Direction candidate_dir;
        // shitty fix so that it's facing towards the enemy as the first option
        // != for null
        if (!Nav.directions0explored) {
            candidate_dir = bc.bcDirectionRotateRight(Nav.dirToMapLoc(building, Globals.enemy_init_loc.get(0)));
        } else {
            candidate_dir = Player.getRandomDir();
        }

        // releases the garrisoned unit if a direction is available
        Unit friend;
        for (int i=0; i<Globals.NUM_DIRECTIONS; i++) {
            candidate_dir = bc.bcDirectionRotateLeft(candidate_dir);

            if (Player.gc.canUnload(building_id, candidate_dir)) {
                Player.gc.unload(building_id, candidate_dir);
                return true;
            }

            // if there's a friendly unit there. Tell them to move
            MapLocation candidate_loc = Nav.getMapLocFromId(building_id).add(candidate_dir);

            if (Player.gc.hasUnitAtLocation(candidate_loc) &&
                    (friend = Player.gc.senseUnitAtLocation(candidate_loc)).team() == building.team()) {
                if (Nav.politelyAskToMove(friend, candidate_dir) && Player.gc.canUnload(building_id, candidate_dir)) {
                    Player.gc.unload(building_id, candidate_dir);
                    //Nav.moveToEnemyBase(Player.gc.senseUnitAtLocation(Nav.getMapLocFromId(factory_id).add(candidate_dir)).id());
                    return true;
                }
            }
        }

        return false;

    }
}
