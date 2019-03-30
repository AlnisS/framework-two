import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages all of the robot's aspects by holding all system data and calling the
 * appropriate updating methods at the right times.
 */
public class Manager {
    /**
     * Top-level Subsystems without an actual owner. This is used as the start
     * for recursive running-through of Subsystems for a specific in or out
     * iteration direction.
     */
    Set<Subsystem> topSubsystems;
    /**
     * All Subsystems (including <code>topSubsystems</code>). This is used for
     * general updates which can happen across all subsystems in any order.
     */
    Set<Subsystem> allSubsystems;

    /**
     * Creates a new Manager using already constructed Subsystems in appropriate
     * hierarchies. Each Subsystem should have all of its Subsystems (and so on)
     * initialized appropriately with appropriate owner references because that
     * is (hopefully) verified during Manager construction.
     *
     * @param topSubsystems Any number of top-level Subsystems this manager will
     *                      be in charge of. Each should already be initialized
     *                      and have its hierarchies set up.
     */
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

    /**
     * Doesn't do anything yet. This will be more applicable with the
     * implementation of a computer vision system.
     */
    public void init() {

    }

    /**
     * Runs over the Subsystems and makes them work and do stuff (hopefully).
     * The overall sequence is: basic data updates -> data requests -> logic
     * updates -> action requests -> actuation -> cleanup. In more detail, it
     * is:
     *
     * <p><b>Basic Data Update: </b>
     * First, the basic data of all Subsystems is updated in an outward
     * direction. The innermost and most basic subsystems update first (such as
     * sensor implementations), then the update propagates outward (such as to
     * the Sensor Subsystem (which manages filtering etc.) and other higher
     * level systems. Each Subsystem is free to call getter methods in its
     * owned Subsystems as they have already completed their basic updates.
     * State updates in other Subsystems are illegal.</p>
     *
     * <p><b>Data Requesting and Responding: </b>
     * Next, Data requests are all sent, then all received and responded to.
     * All data requests are sent in an arbitrary order and should not cause
     * immediate any sort of immediate state updates. After all requests are
     * sent, all Subsystems are asked to respond to those requests (again, all
     * in arbitrary order and no remote state updates allowed) by filling out
     * the data field in each message with the requested data and providing
     * extra info in the result field if needed. Subsystems should hold onto a
     * reference of each data request message they send to have access to the
     * result later on.</p>
     *
     * <p><b>Logic Update: </b>
     * After the Data is sorted out, it is time for the updating of logic!
     * Subsystems now have an opportunity to respond to the results of all of
     * the data requests they sent. However, this is responding in an internal
     * way, not in a communicative way! This is because this, too happens in an
     * arbitrary order, so the state of other Subsystems is non-deterministic at
     * this point in time! Stuff done in this sector should include state
     * machine updates, decisions on what actions to take, and other such
     * important thinking tasks. Nothing should actually happen yet though, and
     * although it is reasonable for higher-level control models to update (such
     * as state monitoring/action planning ones), ones focused on direct
     * interaction with the world should wait until later (after Action requests
     * have been delivered).</p>
     *
     * <p><b>Action Requesting and Execution: </b>
     * Once Subsystems know where they're at and know what they're doing,
     * they can ask others to work to complete their hopes and dreams. First,
     * Action requests are filed in a very similar manner to Data requests, but
     * in this case, the data field of the message will most likely be populated
     * as performing an Action most likely means doing something specific. The
     * requests are sent in a deterministic order: they propagate downward
     * through the hierarchy so that lower Subsystems can react to the requests
     * of the higher ones. This happens in a cascading way: messages are sent,
     * the next level down receives them, that level sends them, the further
     * next level receives them, etc.
     *
     * For a drivebase-based example, the Drivebase may request forward motion
     * from both sides (in a tank drive or similar situation) where then each
     * DriveSide receives this request and propagates the appropriate speed
     * requests down to each Motor Subsystem which then finally updates some of
     * its personal state variables as a note for what to do when the time comes
     * to update the control models. Finally, in another pass (in
     * non-deterministic order), all Subsystems get one more pass over so that
     * lower level subsystems which sent requests to higher ones get heard out
     * by the higher ones (but their requests remain stowed away until the next
     * loop cycle). It would likely be rather rare for an action to be received
     * during this time as that breaks the consistent top-down organization of
     * the hierarchy and reports on what happened should be placed within the
     * result field of the requesting message.</p>
     *
     * <p><b>Acting on the Requests---Control Model Updating and Actuation: </b>
     * Now comes the time to act on all of the requested actions. This means
     * first updating control models (which would mostly be the forward parts of
     * controllers, not the passive, sensing aspects such as speed, position,
     * etc.) and then acting on those updates. For lower level Subsystems, this
     * likely means updating manually implemented PIDf controllers and fancy
     * predictive ones. For higher-up ones, this means looking at the reports in
     * the results field of the action request messages and storing appropriate
     * information for the next update in the logic update phase. After that,
     * physical actuators are updated. This means sending motor powers and servo
     * targets to the physical things on the robot and probably nothing else.
     * There shouldn't be data side effects from this. This should be isolated
     * such that if it isn't called, everything continues running correctly. A
     * case for not calling this would be if the actual physical robot needs to
     * be manually moved for testing or if a mechanism is out of order.</p>
     *
     * <p><b>Cleanup: </b>
     * The final part is the call of the cleanup function. It doesn't have a
     * specific purpose. It's mostly there as a just-in-case thing for code
     * which doesn't fit in elsewhere.</p>
     */
    public void loop() {
        // basic updates sector
        for (Subsystem subsystem : topSubsystems)
            subsystem.updateData();

        // data requests sector
        for (Subsystem subsystem : allSubsystems)
            subsystem.sendDataRequest();
        for (Subsystem subsystem : allSubsystems)
            subsystem.receiveDataRequestMessages();

        // logic update sector
        for (Subsystem subsystem : allSubsystems)
            subsystem.updateLogic();

        // action request sector
        // TODO: make this behave like the comment describes
        for (Subsystem subsystem : topSubsystems)
            subsystem.sendActionRequests();
        for (Subsystem subsystem : allSubsystems)
            subsystem.receiveActionRequestMessages();

        // actuation sector
        for (Subsystem subsystem : allSubsystems)
            subsystem.updateControlModels();
        for (Subsystem subsystem : allSubsystems)
            subsystem.publishControl();

        // cleanup and utility sector
        for (Subsystem subsystem : allSubsystems)
            subsystem.cleanup();
    }
}
