import bc.*;

public class BotRanger extends AttackingBot {


    public static void update(Unit unit) {
        basicAttack(unit.id());
    }
}
