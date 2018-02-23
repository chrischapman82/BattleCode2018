import java.util.ArrayList;
import bc.*;

public class Map {

    // this stores stuff to do with the map I guess


    public static ArrayList<MapLocation> karboniteMap;
    public static ArrayList<Integer> karboniteOnTile;
    public static boolean isKarboniteLeft = true;


    public static final int X_INC = 1;          // for incrementing x and y
    public static final int Y_INC = Globals.planet_width;

    public static void init_maps(PlanetMap planet) {
        karboniteMap = createKarboniteLocMap(planet);
        karboniteOnTile = createKarboniteOnTileMap(planet);
        printMap();

    }


    // new map
    // Should be easier for me to use
    // TODO can see issues with a barrier in the way
    public static ArrayList<Integer> createKarboniteOnTileMap(PlanetMap planet) {

        if (Globals.planet_name == Planet.Mars) {
            // do nothing
            return null;
        }

        ArrayList<Integer> map = new ArrayList<>(Globals.planet_size);
        for (int i=0; i<Globals.planet_size; i++) {

            // stores the amount of karbonite at a certain location
            long curr_karbonite = planet.initialKarboniteAt(new MapLocation(Globals.planet_name, getXFromIndex(i), getYFromIndex(i)));
            map.add((int)curr_karbonite);
        }
        return map;
    }


    public static void printMap() {

        for (int i=0; i<karboniteOnTile.size(); i++) {
            System.out.format("|%2d", karboniteOnTile.get(i));

            if (i % Globals.planet_width == 0) {
                System.out.println();
                for (int j = 0; j < Globals.planet_width; j++) {
                    System.out.format("---");
                }
                System.out.println();
            }
        }
    }

    public static MapLocation getClosestKarbonite(MapLocation unit_loc) {
        int closest_dist = Integer.MAX_VALUE;
        MapLocation closest = null;

        MapLocation curr_map;
        int curr_dist;
        for (int i=0; i<karboniteMap.size(); i++) {
            curr_map = karboniteMap.get(i);
            if ((curr_dist = (int)curr_map.distanceSquaredTo(unit_loc)) < closest_dist) {
                closest_dist = curr_dist;
                closest = curr_map;
            }
        }

        if (closest == null) {
            // there's nothing left to mine!
            isKarboniteLeft = false;
        }

        return closest;
    }

    // after a worker mines, removes
    public static void removeKarboniteFromMap(MapLocation karbonite_loc) {

        int index = getIndex(karbonite_loc);
        int GATHER_RATE = 4;        //fuck checking this. Will probably miss out on like 1 max
        int old_karbonite = karboniteOnTile.get(index);

        int new_karbonite = old_karbonite - GATHER_RATE;

        // changes the current karbonite on the tile!
        karboniteOnTile.set(index, new_karbonite);

        if (new_karbonite <= 0) {
            karboniteMap.remove(karbonite_loc);
        }
    }

    public static ArrayList<MapLocation> createKarboniteLocMap(PlanetMap planet) {


        ArrayList<MapLocation> map = new ArrayList<>(Globals.planet_size);
        MapLocation curr_loc;
        for (int i=0; i<Globals.planet_size; i++) {

            // 1 and 0 i believe but fuck em
            // 1 for yes, 0 for no
            curr_loc = new MapLocation(Globals.planet_name, getXFromIndex(i), getYFromIndex(i));
            if (planet.initialKarboniteAt(curr_loc) != 0) {
                map.add(curr_loc);
            }
        }

        // there's nothing here!
        if (map.size() == 0) {
            Globals.karbonite_left = false;
        }
        return map;
    }


    public static int getIndex(MapLocation loc) {
        return (int)(loc.getX()*X_INC + loc.getY()*Y_INC);
    }

    public static int getXFromIndex(int index) {
        return (index%Globals.planet_width);
    }

    public static int getYFromIndex(int index) {
        return (index/Globals.planet_width);
    }

    // will have O(1) lookup this way.
        /*for (int i=0; i<planet_size; i++) {
            if ((planet.initialKarboniteAt(new MapLocation(Planet.planet, i%planet_width,i*planet_width))) == 1) {
                initKarboniteSpots.add(true);
            } else {
                initKarboniteSpots.add(false);
            }
        }*/
}
