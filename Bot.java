import bc.*;

public class Bot extends Unit {

    public Bot() {

    }

    // retreating when the going gets tough
    public static boolean retreat(Unit unit) {

        // get the enemy direction so I can run away!
        VecUnit enemies = getViewableEnemies(unit);

        if ((enemies.size() == 0)) {
            return false;
            // no need to run away.
        }
        Unit enemy = chooseClosestEnemy(unit, enemies);
        Direction dir_to_enemy = unit.location().mapLocation().directionTo(enemy.location().mapLocation());

        Direction retreat_dir = bc.bcDirectionOpposite(dir_to_enemy);
        // try to run the opposite dir
        return Nav.tryToRetreat(unit, retreat_dir);     // so that can change for each bot
    }

    // basically an abstract method
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

    public static VecUnit getViewableAllies(Unit unit) {
        return Player.gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), unit.visionRange(), Globals.us);
    }

    public static VecUnit getViewableAlliesInRange(Unit unit, int range) {
        return Player.gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), range, Globals.us);
    }
    public static VecUnit getAttackableEnemies(Unit unit) {
        return Player.gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), unit.attackRange(), Globals.them);
    }


    // because they somehow don't have this implemented
    public static boolean isAttackingBot(UnitType unit_type) {
        switch (unit_type) {
            case Factory:
                return false;
            case Rocket:
                return false;
            case Worker:
                return false;
            case Healer:
                return false;
            default:
                return true;
        }
    }

    /* TODO: Hopefully will be better with new api. Otherwise have to create new array list and add to that
    public static VecUnit getViewableEnemiesOfType(Unit unit, UnitType unit_type) {
        VecUnit enemies = getViewableEnemies(unit);
        VecUnit enemies_new;
        for (int i=0; i<enemies.size(); i++) {
            //if ((enemies.get(i)).unitType().equals(unit_type) {

            //}
        }
        return enemies;

    }*/

    // basically a max fn lul

    public static Unit chooseClosestEnemy(Unit unit, VecUnit enemies) {

        if (enemies.size() == 0) {
            return null;
        }
        // some shitty ass finding of the prio unit
        Unit priority_enemy = enemies.get(0);
        float priority_dist = unit.location().mapLocation().distanceSquaredTo(priority_enemy.location().mapLocation());
        Unit curr_enemy;
        float curr_dist;

        for (int i=1; i<enemies.size(); i++) {
            curr_enemy = enemies.get(i);

            // compares the distance between the unit and the enemy with the old closest
            if ((curr_dist = unit.location().mapLocation().distanceSquaredTo(curr_enemy.location().mapLocation()))
                    < priority_dist) {
                priority_enemy = curr_enemy;
                priority_dist = curr_dist;
            }
        }
        return priority_enemy;
    }

    public static Unit chooseClosestAttackableEnemy(Unit unit, VecUnit enemies) {

        if (enemies.size() == 0) {
            return null;
        }

        // some shitty ass finding of the prio unit
        Unit priority_enemy = null;
        float priority_dist = unit.location().mapLocation().distanceSquaredTo(priority_enemy.location().mapLocation());
        Unit curr_enemy;
        float curr_dist;
        long min_range;

        for (int i=0; i<enemies.size(); i++) {
            curr_enemy = enemies.get(i);

            // setting up the min range
            // TODO move to ranger
            if (unit.unitType().equals(UnitType.Ranger)) {
                min_range = unit.rangerCannotAttackRange();
            } else {
                min_range = 0;
            }

            // compares the distance between the unit and the enemy with the old closest
            if ((curr_dist = unit.location().mapLocation().distanceSquaredTo(curr_enemy.location().mapLocation()))
                    < priority_dist && curr_dist > min_range) {

                priority_enemy = curr_enemy;
                priority_dist = curr_dist;
                }

            }
        return priority_enemy;
    }
}
