import bc.*;

public class Bot extends Unit {

    public Bot() {

    }


    public static void update(Unit unit) {

    }

    // marks the enemy for death
    public static boolean markUnit(Unit unit) {
        Globals.priorityEnemies.add(unit);
        return true;
    }



}
