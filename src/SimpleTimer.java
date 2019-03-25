public class SimpleTimer extends Subsystem {
    private long startTime = System.nanoTime();
    private long time = 0;

    public SimpleTimer(Subsystem owner) {
        super(owner);
    }

    protected void updateSelfData() {
        time = System.nanoTime() - startTime;
    }

    protected void sendDataRequest() {}

    void receiveDataRequest(Msg message) {
        if (message.data == Data.SIMPLE_TIME)
            ((Subsystem) message.identifier)
                    .answer(new Msg("SimpleTimer SIMPLE_TIME", time));
    }

    void receiveDataAnswer(Msg message) {}

    void updateLogic() {}

    void sendActionRequest() {}

    void receiveActionRequest(Msg message) {

    }

    void receiveActionAnswer(Msg message) {

    }

    void updateControlModels() {

    }

    void publishControl() {

    }

    void cleanup() {

    }

    public enum Data {SIMPLE_TIME}
}
