import bc.*;

public class BotKnight extends AttackingBot{



    public static void update(Unit unit) {
        basicAttack(unit.id());
    }



    //TODO: delete
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

}
