import bc.Direction;

public class Bot extends bc.Unit {

    public Bot() {

    }


    public void update() {

    }

    public void wander(int id) {
        if (Player.gc.isMoveReady(id)) {
            int NUM_TRIES = 20;
            // just wander I guess. Which sounds pretty shit tbh

            for (int k=0; k<NUM_TRIES; k++) {
                Direction dir = Player.getRandomDir();
                //if (gc.canMove(id, dir)) {
                    //gc.moveRobot(id, dir);
            }
        }

    }


}
