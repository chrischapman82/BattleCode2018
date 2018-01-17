import bc.*;

public class StructFactory extends Structure{

    public static void update(Unit factory) {

        releaseGarrisonUnits(factory);

        // should always produce units. No point in not!


        if (produceUnits(factory.id())) {
            return;
        }


        // code to do different for each


        /*
        if (factory.team() == Team.Blue) {

            if (produceRobot(factory.id(), UnitType.Knight)) {
                return;
            }
        } else {
            if (produceRobot(factory.id(), UnitType.Ranger)) {
                return;
            }
        }*/
    }

    // controls logic for what units I want to produce
    public static boolean produceUnits(int factory_id) {

        if (Player.gc.karbonite() < 20) {
            return false;
        }

        if (Player.gc.karbonite() < 100 && Player.gc.round() > Research.rocketAvailableRound) {
            return false;
        }

        // if we have no workers, make some
        if (Player.gc.karbonite() >= 25 && Globals.getNumUnitsOfType(UnitType.Worker) == 0) {
            return produceRobot(factory_id, UnitType.Worker);
        }

        // mix in a few knights here and there
        if (Player.gc.round() > Research.knightBuildRound) {
            if (Globals.getNumUnitsOfType(UnitType.Knight) < Globals.getNumUnitsOfType(UnitType.Ranger)/2) {
                return produceRobot(factory_id, UnitType.Knight);
            }
        }
        return produceRobot(factory_id, UnitType.Ranger);
    }

    //Produce a unit if it's possible
    //Have to check if a factory can unload a unit and build a unit in the same turn
    public static boolean produceRobot(int factory_id, UnitType unit_type) {

        // TODO: Create workers if we run out
        if ((Player.gc.canProduceRobot(factory_id, unit_type))) {
            Player.gc.produceRobot(factory_id, unit_type);
            Globals.countUnit(unit_type);
            // TODO count these?
            return true;
        }
        return false;
    }
}
