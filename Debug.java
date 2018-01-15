public class Debug {

    // I'd like to use these if they get some visual stuff
    public static void indicateDot(){

    }

    // Error printing
    public static void printMessage(String message) {
        System.out.println(message);
    }


    public static void getTimeLeft() {
        System.out.format("Time left = ");
        System.out.println(Player.gc.getTimeLeftMs());
    }
}
