public class Msg {
    public Enum identifier;
    public Object data;

    public Msg(Enum identifier, Object data) {
        this.identifier = identifier;
        this.data = data;
    }
}
