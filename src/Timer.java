public class Timer extends Subsystem {
    long startTime = System.nanoTime();
    long pauseTime = 0;

    public Timer(Subsystem owner) {
        super(owner);
        addSubsystem(new SimpleTimer(this));
    }

    public enum Actions {PRINT, PAUSE, START, RESET}
}
