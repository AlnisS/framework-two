/**
 * Just holds the main loop.
 */
public class Main {
    /**
     * Just the main method.
     * @param args These don't do anything yet.
     */
    public static void main(String[] args) {
        Subsystem timer = new Timer(null);
        Manager manager = new Manager(timer);
    }
}
