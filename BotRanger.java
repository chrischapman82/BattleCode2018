import bc.*;

public class BotRanger extends AttackingBot {

    public static final int LOW_HP = 60;

    public static void update(Unit unit) {

        // mark those pesky mages for death
        VecUnit viewable_enemies = Player.gc.senseNearbyUnitsByTeam(unit.location().mapLocation(), unit.visionRange(), Globals.them);
        Unit enemy;
        for (int i=0; i<viewable_enemies.size(); i++) {
            enemy = viewable_enemies.get(i);

            if (enemy.unitType().equals(UnitType.Mage) || enemy.unitType().equals(UnitType.Healer)) {
                markUnit(enemy);
            }
        }


        AttackingBot.update(unit);

        // focus fire is good, so always have at least 1 enemy stored

    }

}
