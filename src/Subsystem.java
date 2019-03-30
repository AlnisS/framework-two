import java.util.HashSet;
import java.util.Set;

/**
 * The template for all Subsystems in the hierarchy of control of the robot. The
 * Subsystems are updated by the Manager class which ensures that the
 * appropriate functions are called at the appropriate times. A Subsystem may be
 * anything as big as an entire arm assembly to as atomic as a single motor or
 * sensor. It is permitted to create abstract Subsystems which add more
 * functionality. A generic Sensor Subsystem would be a very appropriate use.
 * An important note: Subsystems are allowed to hold direct references to any
 * other Subsystem as its own class (i.e. referring to a Drivebase subsystem as
 * a Drivebase subsystem rather than a generic Subsystem) and to call any
 * methods in that class (except loop-control methods defined in this Subsystem
 * superclass). However, these methods should only be getter ones with no side
 * effects as any state updates must be handled through the message system.
 */
public abstract class Subsystem {
    /**
     * The Subsystem which contains this Subsystem in its subsystems Set. There
     * shall be one and only one (unless this is a top-level Subsystem---in this
     * case, it shall be null).
     */
    private Subsystem owner;
    /**
     * All Subsystems this Subsystem is in charge of. For example, an Arm
     * Subsystem may have a few joint Subsystems and a collector Subsystem with
     * each of those having more, more atomic Subsystems in their subsystems
     * Sets.
     */
    private Set<Subsystem> subsystems;
    /**
     * Incoming Data and Action request messages.
     */
    private Set<Msg> requestInbox;

    /**
     * Ensures that when the default constructor is implicitly called at the
     * beginning of an extending class, it throws an error because the
     * Subsystem needs to know its owning subsystem.
     */
    public Subsystem() {
        throw new IllegalArgumentException("Subsystem must specify owner.");
    }

    /**
     * Should be called at the beginning of the constructor of any extending
     * class with appropriate owner.
     *
     * @param owner The Subsystem which holds this Subsystem in its subsystems Set
     */
    public Subsystem(Subsystem owner) {
        this.owner = owner;
        subsystems = new HashSet<>();
        requestInbox = new HashSet<>();
    }

    /**
     * Registers subsystem for proper updating. It is ok to hold references
     * elsewhere to a subsystem added by this. For example,
     * <code>addSubsystem(this.someField = new SomeSubsystem(this));</code>
     *
     * @param subsystem
     */
    final public void addSubsystem(Subsystem subsystem) {
        subsystems.add(subsystem);
    }

    //TODO: make some check not only for an owner, but only one actual owner.

    /**
     * Verifies that this Subsystem and its subsystems have a properly assigned
     * owner.
     *
     * @param owner Subsystem which should contain this Subsystem in its
     *              subsystems Set.
     * @return Whether this Subsystem and its subsystems are compliant (true is yes).
     */
    final boolean verify(Subsystem owner) {
        return verifyDown() && verifySelf(owner);
    }

    /**
     * Verifies the Subsystems in <code>this.subsystems</code> Set.
     *
     * @return Whether all Subsystems in <code>this.subsystems</code> are compliant.
     */
    private boolean verifyDown() {
        for (Subsystem subsystem : subsystems) {
            if (!subsystem.verify(this))
                return false;
        }
        return true;
    }

    /**
     * Checks whether this Subsystem has the correct owner set.
     *
     * @param owner Expected owner
     * @return Whether the actual and expected owners match
     */
    private boolean verifySelf(Subsystem owner) {
        return owner == this.owner;
    }

    /**
     * Puts together all Subsystems recursively contained in <code>this.subsystems</code>.
     *
     * @return Set of all Subsystems contained by this (not including this itself)
     */
    Set<Subsystem> getSubsystems() {
        Set<Subsystem> subsystems = new HashSet<>();
        for (Subsystem subsystem : subsystems) {
            subsystems.addAll(subsystem.getSubsystems());
            subsystems.add(subsystem);
        }
        return subsystems;
    }

    /**
     * Updates basic/preliminary data recursively for <code>this.subsystems</code>
     * and this in that order.
     */
    final void updateData() {
        updateDownData();
        updateSelfData();
    }

    /**
     * Calls the <code>updateData()</code> method in all Subsystems in
     * <code>this.subsystems</code>.
     */
    private void updateDownData() {
        for (Subsystem subsystem : subsystems)
            subsystem.updateData();
    }

    /**
     * Request this Subsystem to respond/act on the specified Msg. This is a
     * multipurpose function for both requesting data during the data request
     * phase and actions during the action request phase.
     *
     * @param message Requested data or action (depending on global phase)
     */
    public void request(Msg message) {
        requestInbox.add(message);
    }

    /**
     * Extending-class-implemented function which does basic data updates within
     * this Subsystem. It is safe to use raw data-getting methods from
     * Subsystems owned by this Subsystem because this has already been called
     * on them.
     */
    void updateSelfData() {
    }

    /**
     * Extending-class-implemented function where all data requests are sent.
     * The intended use is to get data from other Subsystems in preparation
     * for logic updates.
     */
    void sendDataRequest() {
    }

    /**
     * Internal function which handles running through the inbox of requests and
     * calling the <code>receiveDataRequest</code> function.
     */
    void receiveDataRequestMessages() {
        for (Msg message : requestInbox)
            receiveDataRequest(message);
        requestInbox.clear();
    }

    /**
     * Extending-class-implemented function which should handle responding to a
     * request for data in the specified message. This would likely be a switch
     * on the <code>identifier</code> field of the <code>Msg</code>.
     *
     * @param message
     */
    void receiveDataRequest(Msg message) {
    }

    /**
     * Extending-class-implemented function which should update the state
     * machines, target positions, etc. in preparation for taking actions.
     */
    void updateLogic() {
    }

    /**
     * Internal function which first sends requests for actions from this
     * Subsystem, then its dependents.
     */
    void sendActionRequests() {
        sendActionRequest();
        for (Subsystem subsystem : subsystems)
            subsystem.sendActionRequest();
    }

    /**
     * Extending-class-implemented function which should send requests to all
     * Subsystems from which action is needed. It is safe to send action
     * requests to this Subsystem's dependent subsystems, and this is the normal
     * case where Action requests propogate downward through a hierarchy. It is
     * possible to send requests upward, but these likely will not have an
     * effect until the next loop cycle because although the higher subsystems
     * will receive the messages this loop cycle, they will not be able to send
     * action requests to other subsystems to actually execute the requests
     * until the next loop cycle.
     */
    void sendActionRequest() {
    }

    /**
     * Internal function for running through inbox and calling the
     * <code>receiveActionRequest</code> function for each one.
     */
    void receiveActionRequestMessages() {
        for (Msg message : requestInbox)
            receiveActionRequest(message);
        requestInbox.clear();
    }

    /**
     * Extending-class-implemented function which responds to an action request.
     * This would probably be best implemented as a switch on the
     * <code>identifier</code> field of the <code>Msg</code>.
     *
     * @param message Request for an action with parameters.
     */
    void receiveActionRequest(Msg message) {
    }

    /**
     * Extending-class-implemented function which should update any control
     * models in response to Actions requested of this Subsystem/any actions
     * this Subsystem will make. This might include manual PIDf controllers,
     * predictive controllers, etc.
     */
    void updateControlModels() {
    }

    /**
     * Extending-class-implemented function which takes the calculated required
     * outputs from the control model update and actually feeds these to
     * actuators this Subsystem controls. This function shouldn't cause state
     * updates to anything except physical objects (although error trapping and
     * logging may be acceptable). The goal is to be able to not call this
     * function and have everything else respond as usual in the case of not
     * wanting to physically run the robot.
     */
    void publishControl() {
    }

    /**
     * Extending-class-implemented utility function which is called at the end
     * of the loop. There is no specific goal for this. It's more just a just-
     * in-case-it's needed thing. It shouldn't send messages to other
     * Subsystems.
     */
    void cleanup() {
    }
}
