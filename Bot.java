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

    public static VecUnit getViewableEnemies(Unit unit) {
        return Player.gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), unit.visionRange(), Globals.them);
    }

    public static VecUnit getAttackableEnemies(Unit unit) {
        return Player.gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), unit.attackRange(), Globals.them);
    }

    public static VecUnit getViewableEnemiesOfType(Unit unit, UnitType unit_type) {
        VecUnit enemies = getViewableEnemies(unit);
        VecUnit enemies_new;
        for (int i=0; i<enemies.size(); i++) {
            //if ((enemies.get(i)).unitType().equals(unit_type) {

            //}
        }
        return enemies;

    }

}
