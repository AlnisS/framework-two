public class SimpleTimer extends Subsystem {
    private long startTime = System.nanoTime();
    private long time = 0;

    public SimpleTimer(Subsystem owner) {
        super(owner);
    }

    public long getTime() {
        return time;
    }

    void updateSelfData() {
        time = System.nanoTime() - startTime;
    }

    void receiveDataRequest(Msg message) {
        if (message.identifier == Data.SIMPLE_TIME)
            message.data = time;
    }

    void receiveActionRequest(Msg message) {
        if (message.identifier == Action.RESET)
            startTime = System.nanoTime();

    }

    public enum Data {SIMPLE_TIME}
    public enum Action {RESET}
}
