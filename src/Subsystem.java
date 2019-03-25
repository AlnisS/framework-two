import java.util.HashSet;
import java.util.Set;

public abstract class Subsystem {
    private Subsystem owner;
    private Set<Subsystem> subsystems;
    private Set<Msg> requestInbox;
    private Set<Msg> answerInbox;

    public Subsystem() {
        throw new IllegalArgumentException("Subsystem must specify owner.");
    }

    public Subsystem(Subsystem owner) {
        this.owner = owner;
        subsystems = new HashSet<>();
        requestInbox = new HashSet<>();
        answerInbox = new HashSet<>();
    }

    final public void addSubsystem(Subsystem subsystem) {
        subsystems.add(subsystem);
    }

    final boolean verify(Subsystem owner) {
        return verifyDown() && verifySelf(owner);
    }

    private boolean verifyDown() {
        for (Subsystem subsystem : subsystems) {
            if (!subsystem.verify(this))
                return false;
        }
        return true;
    }

    private boolean verifySelf(Subsystem owner) {
        return owner == this.owner;
    }

    Set<Subsystem> getSubsystems() {
        Set<Subsystem> subsystems = new HashSet<>();
        for (Subsystem subsystem : subsystems) {
            subsystems.addAll(subsystem.getSubsystems());
            subsystems.add(subsystem);
        }
        return subsystems;
    }

    final void updateData() {
        updateDownData();
        updateSelfData();
    }

    private void updateDownData() {
        for (Subsystem subsystem : subsystems)
            subsystem.updateData();
    }

    public void request(Msg message) {
        requestInbox.add(message);
    }

    public void answer(Msg message) {
        answerInbox.add(message);
    }

    protected void updateSelfData() {}

    protected void sendDataRequest() {}

    void receiveDataRequestMessages() {
        for (Msg message : requestInbox)
            receiveDataRequest(message);
        requestInbox.clear();
    }

    protected void receiveDataRequest(Msg message) {}

    void receiveDataAnswerMessages() {
        for (Msg message : answerInbox)
            receiveDataAnswer(message);
        requestInbox.clear();
    }

    protected void receiveDataAnswer(Msg message) {}

    protected void updateLogic() {}

    protected void sendActionRequest() {}

    void receiveActionRequestMessages() {
        for (Msg message : requestInbox)
            receiveActionRequest(message);
        requestInbox.clear();
    }

    protected void receiveActionRequest(Msg message) {}

    void receiveActionAnswerMessages() {
        for (Msg message : answerInbox)
            receiveActionAnswer(message);
    }

    protected void receiveActionAnswer(Msg message) {}

    protected void updateControlModels() {}

    protected void publishControl() {}

    protected void cleanup() {}
}
