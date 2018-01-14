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

        boolean out_numbered = isOutnumbered(unit, viewable_enemies);

        // Always attack first, no point in not if can(unless mage)

        // if should atk, DO!
        attack(unit, attackable_enemies);

        // if outnumbered, run away
        if (out_numbered) {
            retreat(unit);
            return;
        }
        // if low hp, run after attacking.
        //if (unit.unitType()== UnitType.Ranger && unit.health() < BotRanger.LOW_HP) {
        //    Nav.moveTo(id, unit.location().mapLocation().directionTo(enemy.location().mapLocation()));
        //}

        // If I can see a bunch of enemies try to engage them
        if (engage(unit, viewable_enemies)) {
            return;
        }

        Nav.moveToEnemyBase(unit.id());
        return;
    }

    // gauges whether we're outnumbered
    public static boolean isOutnumbered(Unit unit, VecUnit visible_enemies) {

        // if no enemies, then clearly not
        if (visible_enemies.size() == 0) {
            return false;
        }

        MapLocation unit_loc = unit.location().mapLocation();
        Unit closest_attacking_enemy = visible_enemies.get(0);      // the closest enemy that can attack us
        long closest_dist = Integer.MAX_VALUE;
        int num_attacking_enemies = 0;
        MapLocation closest_attacking_enemy_loc = closest_attacking_enemy.location().mapLocation();

        UnitType type;
        for (int i = 0; i < visible_enemies.size(); i++) {
            Unit enemy = visible_enemies.get(i);

            //TODO possible problems here if blueprint becomes a unit
            if ((type = enemy.unitType()) != UnitType.Factory && type != UnitType.Worker && type != UnitType.Rocket) {
                long distsq = enemy.location().mapLocation().distanceSquaredTo(unit_loc);

                if (distsq <= enemy.attackRange()) {
                    if (distsq < closest_dist) {
                        closest_dist = distsq;
                        closest_attacking_enemy = enemy;
                    }
                    num_attacking_enemies++;
                }
            }
        }

        closest_attacking_enemy_loc = closest_attacking_enemy.location().mapLocation();

        if (num_attacking_enemies == 0) {
            return false;
        }

        int num_attacking_allies = 0;
        if (unit_loc.distanceSquaredTo(closest_attacking_enemy.location().mapLocation()) <= unit.attackRange()) {
            num_attacking_allies++;
        }

        VecUnit visable_allies = getViewableAllies(unit);
        for (int i=0; i<visable_allies.size(); i++) {
            Unit ally = visable_allies.get(i);
            if ((type = ally.unitType()) != UnitType.Factory && type != UnitType.Worker && type != UnitType.Rocket) {

                long distsq = ally.location().mapLocation().distanceSquaredTo(closest_attacking_enemy_loc);

                if (distsq <= ally.attackRange()) {
                    num_attacking_allies++;
                }
            }
        }

        if (num_attacking_allies > num_attacking_enemies) {
            return false;
        }

        if (num_attacking_allies == num_attacking_enemies) {
            // if there's one, only fight if you have the same or more HP
            if (num_attacking_enemies == 1) {

                if (unit.health() >= closest_attacking_enemy.health()) {
                    return false;
                }
            } else {
                return false;
            }
        }
        // if we got to here, we're outnumbered!
        return true;
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
