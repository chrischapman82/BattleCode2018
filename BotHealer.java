import bc.*;
public class BotHealer extends Bot{

    // TODO: make code for healers when they're not complete garbage



    public static void update(Unit unit) {


        // nearby friend
        Unit friend;
        if ((friend = getHealableUnit(unit)) != null) {
            //
        }


    }

    // gets the unit that I shoulld be healing
    // returns null if no one to heal!
    public static Unit getHealableUnit(Unit unit) {

        Unit ally;
        VecUnit allies = getAttackableEnemies(unit);

        // if no allies nearby, do nothing
        if (allies.size() == 0) {
            return null;
        }

        return getLowestHpAlly(allies);
    }


    public static Unit getLowestHpAlly(VecUnit allies) {

        // some shitty ass finding of the prio unit
        Unit priority_ally = null;
        Unit curr_ally;
        long curr_hp;
        long prio_hp = 0;
        for (int i=1; i<allies.size(); i++) {

            curr_ally = allies.get(i);
            curr_hp = curr_ally.maxHealth() - curr_ally.health();

            if (curr_hp > prio_hp) {
                priority_ally = curr_ally;
                prio_hp = curr_hp;
            }
        }

        // for the case no one needs healing
        if (prio_hp == 0) {
            return null;
        }

        return priority_ally;
    }

}
