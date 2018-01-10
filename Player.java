// import the API.
// See xxx for the javadocs.
import bc.*;

public class Player {


    public static Team ally_team;
    public static Team enemy_team;
    public static MapLocation loc0;

    public static void main(String[] args) {

        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
        System.out.println("loc x: "+loc.getX());

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));

        // Connect to the manager, starting the game
        GameController gc = new GameController();

        // Direction is a normal java enum.
        Direction[] directions = Direction.values();

        // Getting the teams for both. Used in some of the given methods
        // Seems like a dumb way to do it, but here we are.
        ally_team = gc.team();
        if (ally_team.equals(Team.Red)) {
            enemy_team = Team.Blue;
        } else {
            enemy_team = Team.Red;
        }


        while (true) {
            System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);

                int id = unit.id(); // reducing the amount of times I have to call this silly function

                // this is actually filthy atm:

                // getting the type of unit:
                // At some point I want to separate units into classes
                switch (unit.unitType()) {
                    case Factory:
                        //if there are any units in the garrison, unload them
                        if ((unit.structureGarrison()).size() > 0 ) {
                            unit.structureGarrison().get(0);

                            //TODO: unload in a random direction so that a blockage won't stop me unloading
                            if (gc.canUnload(id, Direction.North)) {
                                gc.unload(id, Direction.North);
                            }
                        }

                        //Produce a unit if it's possible
                        //Have to check if a factory can unload a unit and build a unit in the same turn
                        if ((gc.canProduceRobot(id, UnitType.Knight))) {
                            gc.produceRobot(id, UnitType.Knight);
                            System.out.println("Producing Knight");
                        }
                        break;

                    // case for the worker
                    case Worker:

                        // if there's a building that can be built nearby, do it
                        VecUnit nearby = gc.senseNearbyUnits(unit.location().mapLocation(), 2);
                        for (int j=0; j<nearby.size(); j++) {
                            Unit other = nearby.get(j);

                            if (gc.canBuild(id, other.id())) {
                                gc.build(id, other.id());
                            }
                        }
                        break;

                    default:


                        /*  1. If can see an enemy, engage them
                                If not in atk range, move towards them
                                If possible, atk enemy
                            2. else just move towards where every one else is going
                         */

                        // by default, if you see an enemy, attack them
                        MapLocation ally_loc = unit.location().mapLocation();
                        VecUnit enemies;

                        // Checks if I can see any enemies
                        if ((enemies = gc.senseNearbyUnitsByTeam(ally_loc, unit.visionRange(), enemy_team)).size() >0) {

                            // chooses the first enemy arbitrarily. Should be sorted at some point
                            Unit enemy = enemies.get(0);
                            MapLocation enemy_loc = enemy.location().mapLocation();

                            // If not in attack range, move towards the enemy
                            if (ally_loc.distanceSquaredTo(enemy_loc) >= unit.attackRange()) {

                                Direction enemy_dir = ally_loc.directionTo(enemy_loc);
                                if (gc.canMove(id, enemy_dir)) {
                                    gc.moveRobot(id, enemy_dir);
                                }

                            }

                            // If I can attack an enemy, do that
                            if (gc.isAttackReady(id) && gc.canAttack(id, enemy.id())) {
                                // can I attack them?
                                gc.attack(id, enemy.id());
                            }
                        } else {
                            int NUM_TRIES = 20;
                            // just wander I guess. Which sounds pretty shit tbh
                            for (int k=0; k<NUM_TRIES; k++) {
                                Direction dir = getRandomDir();
                                if (gc.canMove(id, dir)) {
                                    gc.moveRobot(id, dir);
                            }

                            }
                        }

                        break;

                }
            }
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }




    public static Direction getRandomDir() {
        // fairly sure this isn't random. In fact it's super unlikely to be the last value
        int rand = (int)(Math.random()*(Direction.values().length));
        return Direction.values()[rand];
    }

    // sensing a nearby specific unit
    // senseNearbyUnits

}
