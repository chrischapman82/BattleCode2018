import bc.*;
import java.util.ArrayList;
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
