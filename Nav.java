import bc.*;

import java.util.ArrayList;

public class Nav {

    public static ArrayList<Direction> directions0;      // should pair up with the mapLocation or at least name according to
    public static ArrayList<Direction> directions1;
    public static ArrayList<Direction> directions2;

    public static boolean directions0explored = true;
    public static boolean directions1explored = true;
    public static boolean directions2explored = true;


    // Initialises the map containing directions to the enemy location
    public static void initNavDirections(ArrayList<MapLocation> enemy_loc) {

        // just doing a max of 3 for now
        for (int i=0; i<3; i++) {

            // if no more locations stored, ABORT
            if (i >= enemy_loc.size()) {
                return; // abort
            }

            Bfs bfs = new Bfs(enemy_loc.get(i));
            // should really be an array list but time is of the essence
            if (i==0) {
                directions0 = bfs.doBfs();
                directions0explored = false;
            } else if(i==1) {
                directions1 = bfs.doBfs();
                directions1explored = false;
            } else if (i==2) {
                directions2 = bfs.doBfs();
                directions2explored = false;
            }
        }

        // wtf is happening
        // TODO remove
        for (int i=0; i<enemy_loc.size(); i++) {
            System.out.println("YAASS");
            System.out.println(enemy_loc.get(i));
        }

    }

    // Moving using MapLocation. Calls other fn
    public static boolean moveTo(int id, MapLocation loc) {
        return moveTo(id, getMapLocFromId(id).directionTo(loc));
    }

    // Moves in the given direction
    public static boolean moveTo(int id, Direction dir) {

        Direction cand_dir;

        // try moving normally
        if (tryMoveInDirection(id, dir)) {
            return true;
        }

        // if friendly unit is in our way, politely ask them to move
        Unit friend;
        MapLocation cand_loc = getMapLocFromId(id).add(dir);        // the location i'd like to check

        if ((friend = getFriendlyMovableUnitAtLocation(getMapLocFromId(id))) != null) {

            if (politelyAskToMove(friend, dir)) {
                moveTo(id, dir);
            }
        }
        return false;
    }



    // Checks if there is a friendly unit @ the map location that can move!

    public static Unit getFriendlyMovableUnitAtLocation(MapLocation loc) {
        Unit friend;

        // is there a friendly unit at the map location
        // And the unit is not a rocker or factory, return
        if ((friend = getFriendlyUnitAtLocation(loc)) != null &&
                !(friend.unitType().equals(UnitType.Factory) || friend.unitType().equals(UnitType.Rocket))) {
            return friend;
        }
        return null;
    }

    // if friendly unit at the map loc, returns it.
    // Otherwise, returns null
    public static Unit getFriendlyUnitAtLocation(MapLocation loc) {
        Unit friend;
        if (Player.gc.hasUnitAtLocation(loc) && (friend = Player.gc.senseUnitAtLocation(loc)).team().equals(Globals.us)) {
            return friend;
        }
        return null;
    }

    //public static Unit getUnitAtMapLoc() {
        //return Player.gc.senseUnitAtLocation();
    //}

    // Tells a unit to please move.
    // The unit knows which way it's being pushed.
    public static boolean politelyAskToMove(Unit ally_unit, Direction dir) {

        if (tryMakeWay(ally_unit.id(), dir)) {
            return true;
        }
        return false;
    }

    // for trying to retreat the given unit in the given direction
    public static boolean tryToRetreat(Unit unit, Direction dir) {

        if (unit.movementHeat() >= 10) {
            return false;
        }
        if (tryMoveDirAndPushAlly(unit, dir)) {
            return true;
        }

        Direction dir_left = bc.bcDirectionRotateLeft(dir);
        if (tryMoveDirAndPushAlly(unit, dir_left)) {
            return true;
        }

        Direction dir_right = bc.bcDirectionRotateLeft(dir);
        if (tryMoveDirAndPushAlly(unit, dir_right)) {
            return true;
        }

        return false;
    }

    // checks if there is an ally blocking the way
    // if there is, push them.
    // if was able to move to this location, return true
    // else: false
    public static boolean tryMoveDirAndPushAlly(Unit unit, Direction dir) {

        if (unit.movementHeat() >= 10) {
            return false;
        }

        MapLocation unit_loc = unit.location().mapLocation();
        MapLocation candidate_loc = unit_loc.add(dir);

        // checks if the location is blocked by terrain or off the map or is a building
        if (Tile.isBlocked(candidate_loc)) {
            return false;
        }


        Unit friend;
        if ((friend = getFriendlyMovableUnitAtLocation(unit_loc.add(dir))) != null) {

            if (rudelyAskToMove(friend, dir)) {
                return tryMoveForward(unit.id(), dir);
            }
        } else if (tryMoveForward(unit.id(), dir)) {
            return true;
        }
        return false;
    }

    // Rudely pushes the unit forward.
    // Often used for retreating

    // Unit tries to move in the given direction, asking others to move if required
    public static boolean rudelyAskToMove(Unit ally_unit, Direction dir) {
        if (ally_unit.movementHeat() >= 10) {      // ADD if can move (for factories etc.)
            return false;
        }

        // If there's a mate there, tell them to move.
        // otherwise, move ot teh spot
        Unit friend;

        if (tryMoveDirAndPushAlly(ally_unit, dir)) {
            return true;
        }

        // try to move center left
        Direction dir_left = bc.bcDirectionRotateLeft(dir);
        if (tryMoveDirAndPushAlly(ally_unit, dir_left)) {
            return true;
        }

        Direction dir_right = bc.bcDirectionRotateLeft(dir);
        if (tryMoveDirAndPushAlly(ally_unit, dir_right)) {
            return true;
        }
        return false;
    }

    // tries to make way for a mate.
    // Prioritises moving to the side rather than forward
    //TODO combine with politelyAsk
    public static boolean tryMakeWay(int id, Direction dir) {

        if (Player.gc.unit(id).movementHeat() >= 10) {
            return false;
        }

        // try going left
        if (tryMoveForward(id, bc.bcDirectionRotateLeft((bc.bcDirectionRotateLeft(dir))))) {
            return true;
        }

        // try going right
        if (tryMoveForward(id, bc.bcDirectionRotateRight((bc.bcDirectionRotateRight(dir))))) {
            return true;
        }

        // try going left-centre
        if (tryMoveForward(id, bc.bcDirectionRotateLeft(dir))) {
            return true;
        }

        // try going right-centre
        if (tryMoveForward(id, bc.bcDirectionRotateRight(dir))) {
            return true;
        }

        // try going forward
        if (tryMoveForward(id, dir)) {
            return true;
        }

        return false;
    }

    // Moves in the given direction, or @ a 45deg angle
    public static boolean tryMoveInDirection(int id, Direction dir){

        if (Player.gc.unit(id).movementHeat() >= 10) {
            return false;
        }

        if (Player.gc.canMove(id, dir)) {
            Player.gc.moveRobot(id, dir);
            return true;
        }

        // try going left-centre
        dir = bc.bcDirectionRotateLeft(dir);
        if (tryMoveForward(id, dir)) {
            return true;
        }

        // try going right-centre
        dir = bc.bcDirectionRotateRight(dir);
        if (tryMoveForward(id, dir)) {
            return true;
        }

        return false;
    }

    // try hard to move through
    public static boolean tryHardMoveInDirection(int id, Direction dir) {


        if (Player.gc.unit(id).movementHeat() >= 10) {
            return false;
        }

        if (Player.gc.canMove(id, dir)) {
            Player.gc.moveRobot(id, dir);
            return true;
        }

        // try going left-forward
        Direction dir_left = bc.bcDirectionRotateLeft(dir);
        if (tryMoveForward(id, dir_left)) {
            return true;
        }

        // try going right-forward
        Direction dir_right = bc.bcDirectionRotateRight(dir);
        if (tryMoveForward(id, dir_right)) {
            return true;
        }

        // Try going Left
        Direction dir_left_left = bc.bcDirectionRotateLeft(dir_left);
        if (tryMoveForward(id, dir_left_left)) {
            return true;
        }

        // Try going Right
        Direction dir_right_right = bc.bcDirectionRotateRight(dir_right);
        if (tryMoveForward(id, dir_right_right)) {
            return true;
        }
        return false;
    }



    // if I only have unit
    public static boolean tryGoToMapLocation(Unit unit, MapLocation map_loc) {
        return tryGoToMapLocation(unit.id(), map_loc);
    }

    // Try going to the given MapLocation
    // Very low computational power atm, using no real path finding.
    public static boolean tryGoToMapLocation(int id, MapLocation map_loc) {

        Unit unit = Player.gc.unit(id);
        // we're here
        if (unit.location().mapLocation().equals(map_loc)) {
            return false;
        }
        Direction dir_to_loc = dirToMapLoc(unit, map_loc);

        // TODO: do something if you can't move int hat direction
        return tryHardMoveInDirection(id, dir_to_loc);
    }


    // TODO: move as a group, pushing units along!
    // can do this through checking if loc is blocked by friendly, and getting them to move first

    // Moves to the enemy base.
    // Uses the mapped out placed I used at first
    public static boolean moveToEnemyBase(int id) {

        Direction cand_dir;
        MapLocation curr_loc = getMapLocFromId(id);

        if (Globals.planet_name.equals(Planet.Mars)) {
            wander(id);
            return true;
        }

        // now for earth
        // this is pretty filthy tbh. Array of ArrayLists would be nice
        if (!directions0explored) {
            System.out.println(directions0explored);
            if ((cand_dir = directions0.get(Tile.getIndex(getMapLocFromId(id)))) != null) {

                // for when we've reached the location. Remove from the stored places!
                if (Globals.enemy_init_loc.get(0) == curr_loc) {
                    directions0explored = true;
                    return moveToEnemyBase(id);
                }
                tryMoveInDirection(id, cand_dir);
                return true;
            }
        } else if(!directions1explored) {
            if ((cand_dir = directions1.get(Tile.getIndex(getMapLocFromId(id)))) != null) {
                tryMoveInDirection(id, cand_dir);
                if (Globals.enemy_init_loc.get(1).equals(curr_loc)) {
                    directions1explored = true;
                    return moveToEnemyBase(id);
                }
                return true;
            }
        } else if(!directions2explored) {
            if ((cand_dir = directions2.get(Tile.getIndex(getMapLocFromId(id)))) != null) {
                if (Globals.enemy_init_loc.get(2).equals(curr_loc)) {
                    directions2explored = true;
                    return moveToEnemyBase(id);
                }
                tryMoveInDirection(id, cand_dir);
                return true;
            }
        }
        // if no one left at enemy base, should update messaging.

        return false;
    }


    /*  Wandering around aimlessly
     *  As this gets more sophistocated, should cut down on using this
    */
    public static void wander(int id) {
        if (Player.gc.isMoveReady(id)) {

            // chooses a random direction, and goes in that dir
            Direction dir;

            for (int i=0; i<10; i++) {
                dir = Player.getRandomDir();
                System.out.println(dir);
                if (Player.gc.canMove(id,dir)) {
                    Player.gc.moveRobot(id,dir);
                    return;
                }
            }
        }

    }

                                    /*              HELPER FUNCTIONS            */

    // Moves forward in the target direction if possible
    // Assumes that the move heat has been checked, so make sure to do that first
    // cuts down on comp time
    public static boolean tryMoveForward(int id, Direction dir) {

        if (Player.gc.canMove(id, dir) && Player.gc.unit(id).movementHeat() < 10) {
            Player.gc.moveRobot(id, dir);
            return true;
        }

        return false;
    }

    // helps out using unit
    public static boolean tryMoveForward(Unit unit, Direction dir) {
        return tryMoveForward(unit.id(), dir);
    }

    // gets the direction to a map location for a unit
    public static Direction dirToMapLoc(Unit unit, MapLocation map_loc) {
        return unit.location().mapLocation().directionTo(map_loc);
    }
    // Helper function as getting a map location from id is incredibly frustrating
    // Takes an id, and returns a mapLocation
    public static MapLocation getMapLocFromId(int id) {
        return Player.gc.unit(id).location().mapLocation();
    }

}
