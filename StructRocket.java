import bc.*;

import java.util.ArrayList;

public class StructRocket extends Structure {

    public static ArrayList<MapLocation> landingLocs;   // contains all the possible places to land on mars
    public static int landingLocsCurrIndex = 0;         // where we're up to in the landing locs array
    public static ArrayList<MapLocation> curr_rocket_spots;
    public static ArrayList<MapLocation> rocket_locs;


    public static final int ROCKET_LOW_HP = 60;

    public static void initRocketInfo() {


        // don't do shit for mars
        if (Globals.planet_name.equals(Planet.Mars)) {
            return;
        }

        landingLocs = new ArrayList<>();
        curr_rocket_spots = new ArrayList<>();
        rocket_locs = new ArrayList<>();

        PlanetMap mars_map = Player.gc.startingMap(Planet.Mars);
        int maps_width = (int)mars_map.getWidth();
        MapLocation curr_loc;
        // assumes there's always somewhere to land on mars!
        // TODO. This is bad. If 0,0 is bad, then we might just go to some shitty ass spots
        for (int i=0; i<mars_map.getHeight()*mars_map.getWidth(); i++) {
            curr_loc = new MapLocation(Planet.Mars, i% maps_width, i/maps_width);
            if (mars_map.isPassableTerrainAt(curr_loc) == 1) {
                landingLocs.add(curr_loc);
            }
        }
    }

    // TODO choose location method


    // logic for rocket
    public static void update(Unit rocket) {

        // For mars, just unload garison
        if (Globals.planet_name.equals(Planet.Mars)) {
            releaseGarrisonUnits(rocket);
            return;
        }

        // For Earth
        // just chooses a random, open spot to launch at

        int num_tries = 30;

        // Goes through a bunch of random spots to see if I can land there.
        // Randomness is important so that we don't land on the same spots
        for (int i=0; i<num_tries; i++) {
            landingLocsCurrIndex = Player.rand.nextInt(landingLocs.size());
            if (hasLandedAtMapLoc(landingLocs.get(landingLocsCurrIndex))) {
                continue;
            }
        }

        // get the given landing loc
        MapLocation cand_landingLoc = landingLocs.get(landingLocsCurrIndex);
        if (shouldLaunch(rocket, cand_landingLoc)) {
            Player.gc.launchRocket(rocket.id(), cand_landingLoc);
        }
    }

    // Checks if the given maplocation has already been chose by one of our rockets
    // Important, as if I land on the same spot as another rocket, both rockets are destroyed!
    public static boolean hasLandedAtMapLoc(MapLocation landing_loc) {

        // If no spots have been done before
        if (curr_rocket_spots.size() == 0) {
            return true;
        }

        // if the spot has already been chosen by one of our rockets, don't get there.
        if (curr_rocket_spots.contains(landing_loc)) {
            return false;
        }

        return true;
    }


    // if hp is low launch
    // if the boss has said go, go
    // if have 8 units inside, go

    public static boolean shouldLaunch(Unit rocket, MapLocation loc) {


        // It's's time to go if full garrison
        // TODO push away mates.

        // If it can't launch, just don't
        if (!Player.gc.canLaunchRocket(rocket.id(), loc)) {
            return false;
        }

        // If full garrison. Time to go.
        if (rocket.structureGarrison().size() == MAX_GARRISN_UNITS) {
            return true;
        }

        // If low hp, go time
        if (ROCKET_LOW_HP <= 60) {
            return true;
        }

        return true;
    }

}
