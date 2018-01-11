import bc.*;

public class BotKnight extends Bot{



    public static void update(Unit unit) {

        int id = unit.id();
        MapLocation curr_loc = unit.location().mapLocation();
        VecUnit enemies;

        // Checks if I can see any enemies
        if ((enemies = Player.gc.senseNearbyUnitsByTeam(curr_loc, unit.visionRange(), Globals.them)).size() > 0) {
            System.out.println("I have spotted an enemy");

            //TODO: Don't just choose the first unit
            //Choose based off of hp, closeness
            Unit enemy = enemies.get(0);
            attack(unit, enemy);

        } else {
            Nav.moveToEnemyBase(id);

        }
    }

    public static boolean attack(Unit unit, Unit enemy) {

        MapLocation enemy_loc = enemy.location().mapLocation();
        MapLocation curr_loc = unit.location().mapLocation();

        // If not in attack range, move towards the enemy
        if (curr_loc.distanceSquaredTo(enemy_loc) >= unit.attackRange()) {

            Nav.tryGoToMapLocation(unit, enemy_loc);
        }

        // If I can attack an enemy, do that
        return tryAttack(unit.id(), enemy.id());
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

}
