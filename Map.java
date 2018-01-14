import java.util.ArrayList;
import bc.*;

public class Map {

    // this stores stuff to do with the map I guess


    // new map
    // Should be easier for me to use
    // TODO can see issues with a barrier in the way
    public static ArrayList<Boolean> createKarboniteMap(PlanetMap planet) {

        ArrayList<Boolean> map = new ArrayList<>(Globals.planet_size);
        for (int i=0; i<Globals.planet_size; i++) {

            // 1 and 0 i believe but fuck em
            // 1 for yes, 0 for no
            map.add((planet.initialKarboniteAt(new MapLocation(Globals.planet_name, getXFromIndex(i), getYFromIndex(i)))) == 1);
        }

        return map;
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
