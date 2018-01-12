import bc.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Collections;


public class Bfs {


    public static ArrayList<MapLocation> cameFrom;
    public static ArrayList<Direction> dirFrom;


    public static void doBfs(MapLocation end) {

        /*init*/

        Queue<MapLocation> queue = new LinkedList<>();
        queue.add(end);
        cameFrom = new ArrayList<>();

        for (int i = 0; i < Globals.earth_size; i++) {
            cameFrom.add(null);
        }
        cameFrom.set(Tile.getIndex(end), null);

        Direction dir;
        MapLocation curr;
        MapLocation next;
        while (!queue.isEmpty()) {
            curr = queue.remove();
            dir = Direction.North;  // doesn't really matter, will iterate through all anyway
            for (int i = 0; i < Globals.NUM_DIRECTIONS; i++) {
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
    }

    public static boolean isOpen(MapLocation curr, MapLocation dest) {
    return (Globals.earth.onMap(dest)                   &&      // is it on the map
            cameFrom.get(Tile.getIndex(dest)) == null   &&      // has the tile been visited
            curr.isAdjacentTo(dest))    &&      // Are we actually nex tot the dest
            !(Globals.earth.isPassableTerrainAt(dest) == 0);
    }



    // kind of reversed from usual

   // public static void doBfs(MapLocation end) {

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

    /*public static boolean isOpen(MapLocation curr, MapLocation dest) {
        return (Globals.earth.onMap(dest)                   &&      // is it on the map
                cameFrom.get(Tile.getIndex(dest)) == null   &&      // has the tile been visited
                                curr.isAdjacentTo(dest))    &&      // Are we actually nex tot the dest
                                !(Globals.earth.isPassableTerrainAt(dest) == 0);
    }*/

/*
    public static void reverse(MapLocation goal, MapLocation start) {
        MapLocation curr = goal;
        ArrayList<MapLocation> path = new ArrayList<>();
        while (!curr.equals(start)) {
            path.add(curr);
        }

        path.add(start);
        Collections.reverse(path);
    }*/

    public static void printBfs() {

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
    }

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
