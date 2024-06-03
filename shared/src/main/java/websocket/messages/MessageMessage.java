package websocket.messages;

import chess.ChessGame;

public class MessageMessage extends ServerMessage {

    public final String message;

    private MessageMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    public static MessageMessage joinNotification(String username, ChessGame.TeamColor color) {
        String message = switch (color) {
            case WHITE -> "joined the game as white";
            case BLACK -> "joined the game as black";
            default -> "started observing the game";
        };
        return notification(String.format(message, username));
    }

    public static MessageMessage notification(String notification) {
        return new MessageMessage(ServerMessageType.NOTIFICATION, notification);
    }

    public static MessageMessage error(String error) {
        return new MessageMessage(ServerMessageType.ERROR, "Error: " + error);
    }
}
