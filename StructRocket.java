import bc.*;
public class StructRocket {

    public static MapLocation landingLoc;


    public static void initRocketInfo() {
        landingLoc = new MapLocation(Planet.Mars, 0, 0);
    }


    // TODO choose location method


    // logic for rocket
    public static void update(Unit rocket) {
        if (shouldLaunch(rocket, landingLoc)) {
            Player.gc.launchRocket(rocket.id(), landingLoc);
        }
    }


    // if hp is low launch
    // if the boss has said go, go
    // if have 8 units inside, go

    public static boolean shouldLaunch(Unit rocket, MapLocation loc) {

        if (Player.gc.canLaunchRocket(rocket.id(), loc)) {
            return true;
        }

        return false;
    }

}
