/**
 * Class which holds a message for Subsystem communication. It's pronounced
 * "message." It holds an identifier for what sort of thing the message is
 * requesting of the other subsystem and Data for that request if applicable.
 * Subsystems sending these should hold onto a reference to the message for
 * later access to get the result.
 */
public class Msg {
    /**
     * Some sort of unique tag or identifier for sending or receiving data or an
     * action. The Enum should probably come from the target Subsystem class.
     * For example, it might be of type <code>TargetSubsystem.Action</code>.
     */
    public Enum identifier;
    /**
     * Contains control info for an action request or acts as place to put
     * response data for a request. For example, in a Msg where
     * <code>identifier == TargetSubsystem.Data.POSITION</code>, the response
     * may be a Double or a Float with the position.
     */
    public Object data;
    /**
     * Contains information about the result of some sort of operation. This
     * does not contain requested Data. Instead, it is meant for error passing
     * and other problem handling purposes.
     */
    public Object result;

    /**
     * Use this constructor for an Action message where you want to include both
     * an identifier and some sort of instruction data. This is cleaner than
     * using the default constructor and then setting values.
     * @param identifier Unique identifier for Action specific to receiving
     *                   class.
     * @param data Either control instructions (for Action) or null (for Data
     *             request (though the single argument constructor should be
     *             used for Data requests)).
     */
    public Msg(Enum identifier, Object data) {
        this(identifier, data, null);
    }

    /**
     * Use constructor for a Data request message because those don't need
     * initialized Data. The responding Subsystem should put the answer into the
     * data field for a return.
     * @param identifier Unique identifier for Data specific to receiving class.
     */
    public Msg(Enum identifier) {
        this(identifier, null);
    }

    /**
     * This constructor initializes all fields, but probably wouldn't normally
     * be used because the presence of a non-null <code>result</code> field
     * implies that this message was already handled and that the result is in
     * the <code>result</code> field.
     * @param identifier
     * @param data
     * @param result
     */
    public Msg(Enum identifier, Object data, Object result) {
        this.identifier = identifier;
        this.data = data;
        this.result = result;
    }
}
