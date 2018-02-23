import bc.*;

public class BotKnight extends AttackingBot{



    public static void update(Unit unit) {


        // attack if possible
        if (attack(unit, getAttackableEnemies(unit))) {

        }

        if (engage(unit, getViewableEnemies(unit))) {
            return;
        }

        if (unit.movementHeat() < 10) {
            Nav.moveToEnemyBase(unit.id());
        }
    }

}
