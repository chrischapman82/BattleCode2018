import bc.*;

import java.util.ArrayList;

public class Nav {

    public static ArrayList<Direction> directions;

    // i hate this location maplocation bs
    public static boolean moveTo(int id, MapLocation loc) {
        return moveTo(id, Player.gc.unit(id).location().mapLocation().directionTo(loc));
    }

    public static void initNavDirections(MapLocation enemy_loc) {

        Bfs bfs = new Bfs(enemy_loc);
        directions = bfs.doBfs();
    }


    public static boolean moveTo(int id, Direction dir) {

        Direction cand_dir;
        if ((cand_dir = directions.get(Tile.getIndex(getMapLocFromId(id)))) != null) {
            tryMoveInDirection(id, cand_dir);
            return true;
        }
        // try moving normally
        if (tryMoveInDirection(id, dir)) {
            return true;
        }

        Unit friend;
        // if friendly unit is in our way, ask them to please move
        if ((friend = Player.gc.senseUnitAtLocation(Player.gc.unit(id).location().mapLocation().add(dir))).team().equals(Globals.us)) {
            if (politelyAskToMove(friend, dir)) {
                moveTo(id, dir);
            }
        }
        return false;
    }

    // Tells a unit to please move.
    // The unit knows which way it's being pushed
    public static boolean politelyAskToMove(Unit unit, Direction dir) {

        if (tryMakeWay(unit.id(), dir)) {
            return true;
        }
        return false;
    }

    // tries to make way for a friend.
    // Prioritises moving to the side rather than forward
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

        // try going left
        dir = bc.bcDirectionRotateLeft(dir);
        if (tryMoveForward(id, dir)) {
            return true;
        }

        // try going right
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

    // helper function
    public static boolean tryMoveForward(int id, Direction dir) {

        if (Player.gc.canMove(id, dir)) {
            Player.gc.moveRobot(id, dir);
            return true;
        }

        return false;
    }


    // if I only have unit
    public static boolean tryGoToMapLocation(Unit unit, MapLocation map_loc) {
        return tryGoToMapLocation(unit.id(), map_loc);
    }

    // it sucks switching between unit and id but gotta be done for speed
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


    //public static boolean tryGoToDirection(int id, Direction dir) {

    //}



    // moves to where the enemy was init situated
    public static boolean moveToEnemyBase(int id) {

        return tryGoToMapLocation(id, Globals.enemy_init_loc);
    }

    // gets the direction to a map location for a unit
    public static Direction dirToMapLoc(Unit unit, MapLocation map_loc) {

        return unit.location().mapLocation().directionTo(map_loc);
    }


    public static void wander(int id) {

        //System.out.println("Attempting to wander");
        if (Player.gc.isMoveReady(id)) {
            //dont think is move ready is working
            //if (gc.isMoveReady(id)) {
            int NUM_TRIES = 10;
            // just wander I guess. Which sounds pretty shit tbh
            //System.out.println("I am allowed to wander");

            Direction[] dirs = Direction.values();

            int num_directions = Direction.values().length;
            int rand = (int)(Math.random()*num_directions);
            for (int k=0; k<num_directions; k++) {
                Direction dir = dirs[(rand+k)%num_directions];
                // System.out.format("%s\n", dir);
                if (Player.gc.canMove(id, dir)) {
                    Player.gc.moveRobot(id, dir);
                    //System.out.println("Wandering");
                    break;
                }
            }
        }

    }

    // this has been frustrating af
    public static MapLocation getMapLocFromId(int id) {
        return Player.gc.unit(id).location().mapLocation();
    }

}
