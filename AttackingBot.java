// import the API.
// See xxx for the javadocs.
import bc.*;
public class AttackingBot extends Bot{



    public static void update(Unit unit) {
        basicAttack(unit.id());
    }

    // tries to attack an enemy
    public static boolean tryAttack(int id, int enemy_id) {

        if (Player.gc.isAttackReady(id) && Player.gc.canAttack(id, enemy_id)) {
            // can I attack them?
            Player.gc.attack(id, enemy_id);
            return true;
        }
        return false;
    }


    // returns true if we attacked
    // doesn't take moving into account
    public static boolean basicAttack(int id) {

        Unit unit = Player.gc.unit(id);
        MapLocation curr_loc = unit.location().mapLocation();
        VecUnit attackable_enemies;
        VecUnit viewable_enemies;

        // If any in range, just attack them.
        if ((attackable_enemies = Player.gc.senseNearbyUnitsByTeam(curr_loc, unit.attackRange(), Globals.them)).size() > 0) {
            return tryAttack(id, chooseLowestHpEnemy(attackable_enemies).id());

            // if I can see enemies but not in range, try and get close enough to attack
        } else if ((viewable_enemies = Player.gc.senseNearbyUnitsByTeam(curr_loc, unit.visionRange(), Globals.them)).size() > 0) {

            // currently going towards the closest enemy
            Unit enemy = chooseClosestEnemy(unit, viewable_enemies);
            Nav.tryGoToMapLocation(id, enemy.location().mapLocation());
            return tryAttack(id, enemy.id());

        } else {
            Nav.moveToEnemyBase(id);
        }
        return false;
    }



    /* Choosing certain enemies

     */
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
