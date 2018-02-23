import bc.*;

public class BotRanger extends AttackingBot {

    public static final int LOW_HP = 60;
    public static final int ATTACK_RANGE = 50;

    public static void update(Unit unit) {

        // mark those pesky mages for death
        VecUnit viewable_enemies = getViewableEnemies(unit);
        Unit enemy;
        for (int i=0; i<viewable_enemies.size(); i++) {
            enemy = viewable_enemies.get(i);

            if (enemy.unitType().equals(UnitType.Mage) || enemy.unitType().equals(UnitType.Healer)) {
                markUnit(enemy);
            }
        }

        if (attack(unit, getAttackableEnemies(unit))) {

        }

        if (engage(unit, viewable_enemies)) {
            return;
        }

        Nav.moveToEnemyBase(unit.id());
        return;
        // focus fire is good, so always have at least 1 enemy stored
    }

    public static boolean engage(Unit unit, VecUnit enemies) {

        int id = unit.id();

        if ((enemies.size() > 0)) {
            Unit enemy = chooseClosestEnemy(unit, enemies);

            // go towards the enemy and try to attack the closest enemy hopefully
            Nav.tryGoToMapLocation(id, enemy.location().mapLocation());
            tryAttack(id, enemy.id());      // could use attack - but might be too expensive

            return true;
        }
        return false;
    }

    // TODO: try to micro
    public static boolean tryToMicro(Unit unit) {

        // run away from Knights

        return false;
    }


    

    // TODO: change targeting to do with min range

    // TODO: I'd like to add a better score at one point


    //public static int enemyScore(Unit enemy) {
///    }

}
