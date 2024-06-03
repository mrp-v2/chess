package websocket.messages;

public class MessageMessage extends ServerMessage {

    public final String message;

    private MessageMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    public static MessageMessage notification(String notification) {
        return new MessageMessage(ServerMessageType.NOTIFICATION, notification);
    }

    public static MessageMessage error(String error) {
        return new MessageMessage(ServerMessageType.ERROR, "Error: " + error);
    }
}
