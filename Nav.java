import bc.*;

public class Nav {


    // Moves in the given direction, or @ a 45deg angle
    public static boolean tryMoveInDirection(int id, Direction dir){

        if (Player.gc.canMove(id, dir)) {
            Player.gc.moveRobot(id, dir);
            return true;
        }

        // try going left
        dir = bc.bcDirectionRotateLeft(dir);
        if (Player.gc.canMove(id, bc.bcDirectionRotateLeft(dir))) {
            Player.gc.moveRobot(id, dir);
            return true;
        }

        // try going right
        dir = bc.bcDirectionRotateRight(dir);
        if (Player.gc.canMove(id, dir)) {
            Player.gc.moveRobot(id, dir);
            return true;
        }


        return false;
    }

    // try hard to move through
    public static boolean tryHardMoveInDirection(int id, Direction dir) {

        if (Player.gc.canMove(id, dir)) {
            Player.gc.moveRobot(id, dir);
            return true;
        }

        // try going left-forward
        Direction dir_left = bc.bcDirectionRotateLeft(dir);
        if (Player.gc.canMove(id, bc.bcDirectionRotateLeft(dir))) {
            Player.gc.moveRobot(id, dir);
            return true;
        }

        // try going right-forward
        Direction dir_right = bc.bcDirectionRotateRight(dir);
        if (Player.gc.canMove(id, dir_right)) {
            Player.gc.moveRobot(id, dir_right);
            return true;
        }

        // Try going Left
        Direction dir_left_left = bc.bcDirectionRotateLeft(dir_left);
        if (Player.gc.canMove(id, bc.bcDirectionRotateLeft(dir_left_left))) {
            Player.gc.moveRobot(id, dir_left_left);
            return true;
        }

        // Try going Right
        Direction dir_right_right = bc.bcDirectionRotateRight(dir_right);
        if (Player.gc.canMove(id, dir_right_right)) {
            Player.gc.moveRobot(id, dir_right_right);
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
        return tryMoveInDirection(id, dir_to_loc);
    }




    // moves to where the enemy was init situated
    public static boolean moveToEnemyBase(int id) {

        return tryGoToMapLocation(id, Globals.enemy_init_loc);
    }

    // gets the direction to a map location for a unit
    public static Direction dirToMapLoc(Unit unit, MapLocation map_loc) {

        return unit.location().mapLocation().directionTo(map_loc);
    }


    public static void wander(int id) {

        System.out.println("Attempting to wander");
        if (Player.gc.isMoveReady(id)) {
            //dont think is move ready is working
            //if (gc.isMoveReady(id)) {
            int NUM_TRIES = 10;
            // just wander I guess. Which sounds pretty shit tbh
            System.out.println("I am allowed to wander");

            Direction[] dirs = Direction.values();

            int num_directions = Direction.values().length;
            int rand = (int)(Math.random()*num_directions);
            for (int k=0; k<num_directions; k++) {
                Direction dir = dirs[(rand+k)%num_directions];
                // System.out.format("%s\n", dir);
                if (Player.gc.canMove(id, dir)) {
                    Player.gc.moveRobot(id, dir);
                    System.out.println("Wandering");
                    break;
                }
            }
        }

    }
}
