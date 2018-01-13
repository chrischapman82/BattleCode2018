// import the API.
// See xxx for the javadocs.
import bc.*;
public class AttackingBot extends Bot{

    public static void update(Unit unit) {

        // general vars
        int id = unit.id();
        VecUnit attackable_enemies = getAttackableEnemies(unit);
        VecUnit viewable_enemies = getViewableEnemies(unit);

        Unit enemy;
        int enemy_id;

        // Always attack first, no point in not (unless mage)

        // if should atk, DO!
        if (attack(unit, attackable_enemies)) {
            retreat(unit);      // TODO have logic for when to retreat
            return;
        }

        // if low hp, run after attacking.
        //if (unit.unitType()== UnitType.Ranger && unit.health() < BotRanger.LOW_HP) {
        //    Nav.moveTo(id, unit.location().mapLocation().directionTo(enemy.location().mapLocation()));
        //}
        if (engage(unit, viewable_enemies)) {
            return;
        }


        Nav.moveToEnemyBase(unit.id());
        return;
    }

    public static boolean engage(Unit unit, VecUnit enemies) {

        int id = unit.id();

        if ((enemies.size() > 0)) {
            Unit enemy = chooseClosestEnemy(unit, enemies);

            // if you're a ranger, sit a second
            if (unit.unitType() == UnitType.Ranger && unit.health() < BotRanger.LOW_HP && unit.attackHeat() >= 10) {
                return true;
            }

            // go towards the enemy and try to attack the closest enemy hopefully
            Nav.tryGoToMapLocation(id, enemy.location().mapLocation());
            tryAttack(id, enemy.id());      // could use attack - but might be too expensive

            return true;
        }
        return false;
    }


    public static boolean attack(Unit unit, VecUnit enemies) {


        // hacky fix atm
        int id = unit.id();
        if ((enemies.size() > 0)) {

            // hacky fix atm for Rangers not being able to attack if an enemy is in meelee range
            if (unit.unitType() == UnitType.Ranger) {   // TODO: FIX
                for (int i=0; i < enemies.size(); i++) {
                    if (tryAttack(id, enemies.get(i).id())) {
                        return true;
                    }
                }
                return false;
            }
            Unit enemy = chooseLowestHpEnemy(enemies);
            int enemy_id = enemy.id();
            return (tryAttack(id, enemy_id));
        }
        return false;
    }

    // tries to attack an enemy
    public static boolean tryAttack(int id, int enemy_id) {

        if (Player.gc.isAttackReady(id) && Player.gc.canAttack(id, enemy_id)) {
            // can I attack them?

            checkIfEnemyKilled(Player.gc.unit(id), Player.gc.unit(enemy_id));
            Player.gc.attack(id, enemy_id);
            return true;
        }
        return false;
    }

    // checks if the attack will kill the enemy
    public static boolean checkIfEnemyKilled(Unit unit, Unit enemy) {

        long armour = 0;
        if (enemy.unitType().equals(UnitType.Knight)) {
            armour = enemy.knightDefense();
        }
        if (unit.damage() > (enemy.health() - armour)) {
            // they dead
            Globals.killEnemyUnit(enemy);
            return true;
        }
        return false;
    }



    /* Choosing certain enemies
     */

    // checks for priority enemies that have been marked
    // will return null if no priority enemies are there
    public static Unit choosePriorityEnemy(Unit unit, VecUnit enemies) {

        Unit enemy;
        for (int i=0; i<enemies.size(); i++) {
            enemy = enemies.get(i);
            if (Globals.priorityEnemies.contains(enemy)) {
                return enemy;
            }
        }
        return null;
    }


    // Assumes that enemies is not null.
    // Basically just a find max fn
    public static Unit chooseLowestHpEnemy(VecUnit enemies) {

        // some shitty ass finding of the prio unit
        Unit priority_enemy = enemies.get(0);
        Unit curr_enemy;
        for (int i=1; i<enemies.size(); i++) {

            curr_enemy = enemies.get(i);
            if (curr_enemy.health() < priority_enemy.health()) {
                priority_enemy = curr_enemy;
            }
        }
        return priority_enemy;
    }


}
