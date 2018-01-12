// import the API.
// See xxx for the javadocs.
import bc.*;
public class AttackingBot extends Bot{

    public static void update(Unit unit) {

        // general vars
        int id = unit.id();
        MapLocation curr_loc = unit.location().mapLocation();
        VecUnit attackable_enemies = getAttackableEnemies(unit);
        VecUnit viewable_enemies = getViewableEnemies(unit);

        Unit enemy;
        int enemy_id;

        // If any in range, just attack them.
        if ((attackable_enemies.size() > 0)) {
            enemy = chooseLowestHpEnemy(attackable_enemies);
            enemy_id = enemy.id();
            tryAttack(id, enemy_id);

            // if low hp, run after attacking.
            if (unit.unitType()== UnitType.Ranger && unit.health() < BotRanger.LOW_HP) {
                Nav.moveTo(id, unit.location().mapLocation().directionTo(enemy.location().mapLocation()));
            }
            return;

            // if I can see enemies but not in range, try and get close enough to attack
        }

        // should be part of the ranger code at some point

        if ((viewable_enemies.size() > 0)) {
            enemy = chooseClosestEnemy(unit, viewable_enemies);

            // if you're a ranger, sit a second
            if (unit.unitType()== UnitType.Ranger && unit.health() < BotRanger.LOW_HP && unit.attackHeat()>=10) {
                return;
            }
            Nav.tryGoToMapLocation(id, enemy.location().mapLocation());
            tryAttack(id, enemy.id());
            return;

        } else {
            Nav.moveToEnemyBase(id);
        }
        return;
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

    public static Unit chooseClosestEnemy(Unit unit, VecUnit enemies) {

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
}
