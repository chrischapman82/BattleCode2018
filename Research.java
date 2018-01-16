import bc.*;

public class Research {


    public static int rocketAvailableRound;


    public static void init() {


        Player.gc.queueResearch(UnitType.Worker);       // 25
        Player.gc.queueResearch(UnitType.Ranger);       // 25
        Player.gc.queueResearch(UnitType.Knight);       // 25
        Player.gc.queueResearch(UnitType.Knight);       // 100
        Player.gc.queueResearch(UnitType.Ranger);       // 100
        Player.gc.queueResearch(UnitType.Rocket);       // 100

        rocketAvailableRound = 25+25+25+100+100+100;

    }

}
