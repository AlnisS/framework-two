public class Timer extends Subsystem {
    long pauseTime = 0;
    long lastPauseTime = 0;
    long cumulativeTime = 0;
    Msg timerInfo;
    State state;
    SimpleTimer totalTimer;
    SimpleTimer pauseTimer;

    public Timer(Subsystem owner) {
        super(owner);
        addSubsystem(totalTimer = new SimpleTimer(this));
        addSubsystem(pauseTimer = new SimpleTimer(this));
        state = State.RUNNING;
    }

    @Override
    void updateSelfData() {
        switch (state) {
            case RUNNING:
                cumulativeTime = totalTimer.getTime() - pauseTime;
                break;
            case PAUSED:
                pauseTime = lastPauseTime + pauseTimer.getTime();
        }
    }

    @Override
    void receiveActionRequest(Msg message) {
        switch ((Action) message.identifier) {
            case PRINT:
                System.out.println(cumulativeTime);
                break;
            case PAUSE:
                state = State.PAUSED;
                lastPauseTime = pauseTimer.getTime();
                pauseTimer.request(new Msg(SimpleTimer.Action.RESET));
            case START:
                state = State.RUNNING;

        }
    }

    public enum Action {PRINT, PAUSE, START, RESET}
    private enum State {RUNNING, PAUSED}
}
