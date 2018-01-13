import bc.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Collections;


// Information about the map using breadth first search to find stuff
public class Bfs {


    //public ArrayList<MapLocation> cameFrom;
    public ArrayList<Direction> dirFrom;
    public MapLocation end;

    public Bfs(MapLocation end) {
        this.end = end;
        //cameFrom = new ArrayList<>();
        dirFrom = new ArrayList<>();
    }

    // does breadth first search to find a way from each square to get to the location
    // Is NOT the fastest path atm, but will do for now
    // Gets a map in which each tile has a direction to follow in order to get to the end loc
    public ArrayList<Direction> doBfs() {

        /*init*/
        Queue<MapLocation> queue = new LinkedList<>();
        queue.add(end);                 // Q containing all endings
        dirFrom = new ArrayList<>();    // direction list

        // setting all initial direction to null
        for (int i = 0; i < Globals.earth_size; i++) {
            dirFrom.add(null);
        }
        dirFrom.set(Tile.getIndex(end), null);          // may not be needed

        Direction dir;
        MapLocation curr;
        MapLocation next;

        // iterates through all reachable parts of the map
        // TODO: what happens if end is unreachable
        while (!queue.isEmpty()) {
            curr = queue.remove();
            dir = Direction.North;  // dir doesn't really matter, will iterate through all dirs anyway
            for (int i = 0; i < Globals.NUM_DIRECTIONS; i++) {
                dir = bc.bcDirectionRotateLeft(dir);
                next = curr.add(dir);

                // if tile is open, add to the queue of tiles to check form
                if (isOpen(curr, next)) {
                    queue.add(next);
                    dirFrom.set(Tile.getIndex(next), dir);
                }
            }
        }
        return dirFrom;
    }


    /*  Checks if the given destination is:
     *  on the map, !visited, adjacent to the prev tile and can be moved onto
    */
    public boolean isOpen(MapLocation curr, MapLocation dest) {
    return (Globals.earth.onMap(dest)                   &&      // is it on the map
            dirFrom.get(Tile.getIndex(dest)) == null    &&      // has the tile been visited
            curr.isAdjacentTo(dest))                    &&      // Are we actually nex tot the dest
            !(Globals.earth.isPassableTerrainAt(dest) == 0);    // Can be move onto?
    }



    // kind of reversed from usual
    // Some stuff that I was playing around with that sucked dick.

   // public void doBfs(MapLocation end) {

        /*init*/
    /*
        Queue<MapLocation> queue = new LinkedList<>();
        queue.add(end);
        cameFrom = new ArrayList<>();

        for (int i=0; i<Globals.earth_size;i++) {
            cameFrom.add(null);
        }
        cameFrom.set(Tile.getIndex(end), null);

        Direction dir;
        MapLocation curr;
        MapLocation next;
        while (!queue.isEmpty()) {
            curr = queue.remove();
            dir = Direction.North;  // doesn't really matter, will iterate through all anyway
            for (int i=0; i<Globals.NUM_DIRECTIONS; i++) {
                dir = bc.bcDirectionRotateLeft(dir);
                next = curr.add(dir);

                if (isOpen(curr, next)) {
                    //System.out.println(Globals.earth.isPassableTerrainAt(next));
                    //System.out.println(next.getX());
                    queue.add(next);
                    cameFrom.set(Tile.getIndex(next), curr);
                }
            }

        }

        printBfs();*/
        /*
        for (int i=0; i<cameFrom.size(); i++) {
            if (cameFrom.get(i)== null) {
                dirFrom.add(null);
            } else {
                dirFrom.add(cameFrom.get(i).directionTo(Tile.getMapLocation(i)));
            }
        }

        for (int i=0; i<dirFrom.size(); i++) {
            if (dirFrom.get(i) == null) {
                System.out.println("NULL");
                continue;
            }
            System.out.println(dirFrom.get(i));
        }
    }*/

    // checks whether can move to the node in question.

    /*public boolean isOpen(MapLocation curr, MapLocation dest) {
        return (Globals.earth.onMap(dest)                   &&      // is it on the map
                cameFrom.get(Tile.getIndex(dest)) == null   &&      // has the tile been visited
                                curr.isAdjacentTo(dest))    &&      // Are we actually nex tot the dest
                                !(Globals.earth.isPassableTerrainAt(dest) == 0);
    }*/

/*
    public void reverse(MapLocation goal, MapLocation start) {
        MapLocation curr = goal;
        ArrayList<MapLocation> path = new ArrayList<>();
        while (!curr.equals(start)) {
            path.add(curr);
        }

        path.add(start);
        Collections.reverse(path);
    }*/

/*
    public void printBfs() {

        MapLocation curr;
        for (int i=0; i<Globals.earth_size; i++) {
            curr = cameFrom.get(i);
            if (curr == null) {
                System.out.format("-----|");
            } else {
                System.out.format("%2d,%2d|", cameFrom.get(i).getX(), cameFrom.get(i).getY());
            }

            if (i%Globals.earth_width == Globals.earth_width-1) {
                System.out.println("");
            }
        }
        System.out.println("**");
    }*/

}



/*
public class Bfs {

    public MapLocation start;
    public MapLocation end;
    public ArrayList<Direction> map;
    public PlanetMap planet_map;
    public ArrayList<MapLocation> curr_points;
    public ArrayList<MapLocation> path;
    public ArrayList<Boolean> visited;

    public State state;

    public enum State {
        SPREAD, RETURN
    }

    public void Bfs(MapLocation start, MapLocation end) {
        this.start = start;
        this.end = end;
        curr_points = new ArrayList<>();
        state = State.SPREAD;
        map = new ArrayList<>();
        planet_map = Player.gc.startingMap(Planet.Earth);
        path = new ArrayList<>();

        int map_size = (int)(planet_map.getHeight()*planet_map.getWidth());
        visited = new ArrayList<>(map_size);
        for (int i=0; i<map_size; i++) {
            visited.add(false);
        }

    }

    public boolean isOpen(MapLocation curr_loc, MapLocation loc) {
        // TODO use passable
        return (planet_map.isPassableTerrainAt(loc) == 0 && !loc.equals(curr_loc));
    }

    public boolean nextStep(MapLocation curr_loc) {
        ArrayList<MapLocation> nextPoints = new ArrayList<>();

        Direction dir;
        MapLocation forward;
        for (MapLocation loc : curr_points) {
            dir = Direction.North;
            for (int i = 0; i < Globals.NUM_DIRECTIONS; i++) {
                dir = bc.bcDirectionRotateLeft(dir);
                forward = curr_loc.add(dir);

                if (isOpen(curr_loc, forward)) {
                    nextPoints.add(forward);
                    //ahead.set(self.md,d.index+1)
                }
            }
            if (loc.equals(end)) {
                state = State.RETURN;
            }
        }
        curr_points = nextPoints;

        Direction tileDir;
        if (state.equals(State.RETURN)) {
            MapLocation currentLoc = path.get(path.size());
            tileDir =
        }
    }

    public int getIndex(long x, long y) {
        return ((int)x + (int)y*(int)planet_map.getHeight());
    }


}*/
