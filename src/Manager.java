import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Manager {
    Set<Subsystem> topSubsystems;
    Set<Subsystem> allSubsystems;

    public Manager(Subsystem... topSubsystems) {
        this.topSubsystems = new HashSet<>(Arrays.asList(topSubsystems));
        this.allSubsystems = new HashSet<>();
        for (Subsystem subsystem : topSubsystems)
            subsystem.verify(null);
        for (Subsystem subsystem : this.topSubsystems) {
            this.allSubsystems.addAll(subsystem.getSubsystems());
            this.allSubsystems.add(subsystem);
        }
    }

    public void init() {

    }

    public void loop() {
        for (Subsystem subsystem : topSubsystems)
            subsystem.updateData();

        for (Subsystem subsystem : allSubsystems)
            subsystem.sendDataRequest();
        for (Subsystem subsystem : allSubsystems)
            subsystem.receiveDataRequestMessages();
        for (Subsystem subsystem : allSubsystems)
            subsystem.receiveDataAnswerMessages();

        for (Subsystem subsystem : allSubsystems)
            subsystem.updateLogic();

        for (Subsystem subsystem : allSubsystems)
            subsystem.sendActionRequest();
        for (Subsystem subsystem : allSubsystems)
            subsystem.receiveActionRequestMessages();
        for (Subsystem subsystem : allSubsystems)
            subsystem.receiveActionAnswerMessages();

        for (Subsystem subsystem : allSubsystems)
            subsystem.updateControlModels();
        for (Subsystem subsystem : allSubsystems)
            subsystem.publishControl();
        for (Subsystem subsystem : allSubsystems)
            subsystem.cleanup();
    }
}
