import bc.*;

import java.util.ArrayList;

public class Pathing {

    public ArrayList<Direction> path;
    public ArrayList<Boolean> passable;
    public long closest;
    public MapLocation start;
    public MapLocation end;
    public PlanetMap map;
    public MapLocation curr_loc;
    public Direction curr_dir;

    public State state;

    public enum State {
        FREE, FOLLOW, ARRIVAL
    }

    public void bugPath(MapLocation start, MapLocation dest) {


    }

    // initialising necessary values
    // Based off of code by MaxMann
    public void bugPathInit(MapLocation start_loc, MapLocation end_loc) {

        map = Player.gc.startingMap(Planet.Earth);
        start = start_loc;
        end = end_loc;
        closest = start.distanceSquaredTo(start);
        state = State.FREE;
        path = new ArrayList<>();

        //int map_size = map.getHeight()*map.getWidth();
        //passable = new ArrayList<>(map_size);

        for (int i = 0; i < map.getHeight() * map.getWidth(); i++) {
            // TODO fix for API too
            // hopefully makes this faster
            passable.add(map.isPassableTerrainAt(new MapLocation(Planet.Earth, getX(i), getY(i))) != 0);
        }
    }

    //TODO fix up is passable when API is done
    // checks that the location is on the map and not the current loc
    public boolean isOpen(MapLocation curr, MapLocation loc) {
        // TODO use passable
        return (map.isPassableTerrainAt(loc) == 0 && !loc.equals(curr));
    }

    public void nextStep(MapLocation curr) {
        switch (state) {
            case FREE:
                curr_dir = curr.directionTo(end);
                MapLocation forward = curr.add(curr_dir);
                if (isOpen(curr, forward)) {
                    //self.moveAhead();
                } else {
                    closest = curr.distanceSquaredTo(end);
                    state = State.FOLLOW;
                }
                break;
            case FOLLOW:    // following a wall
                for (int i = 0; i < Globals.NUM_DIRECTIONS; i++) {
                    if (isOpen(curr, curr.add(curr_dir))) {
                        break;
                    }
                    curr_dir = bc.bcDirectionRotateRight(curr_dir);
                }
                //moveAhead
                curr_dir = bc.bcDirectionRotateLeft(curr_dir);

                if (curr.distanceSquaredTo(end) < closest) {
                    state = State.FREE;
                }
            case ARRIVAL:
                break;
        }

        if (curr_loc.equals(end)) {
            state = State.ARRIVAL;

        }
        // could return true if at the end
    }


    public int getX(int index) {
        return (int) (index % map.getWidth());

    }

    public int getY(int index) {
        return (int) (index / map.getWidth());

    }

    /* Using pseudo code from http://web.mit.edu/eranki/www/tutorials/search/
    */
    /*
    public static void UseAStar() {
        PriorityQueue<Tile> openList = new PriorityQueue<>(INITIAL_CAPACITY, new Comparator() {
            public int compare(Tile tile1, Tile tile2) {
                if (( int tot = tile1.f - tile2.f) >1){
                    return -1;
                } else if (tot < 1) {
                    return 1;
                }
                return 0;
            }
        });
    }*/
}
