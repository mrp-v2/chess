package websocket.messages;

import chess.ChessGame;

public class NotificationMessage extends ServerMessage {

    public final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public static NotificationMessage joinNotification(String username, ChessGame.TeamColor color) {
        String message = switch (color) {
            case WHITE -> "joined the game as white";
            case BLACK -> "joined the game as black";
            default -> "started observing the game";
        };
        return new NotificationMessage(String.format("%s " + message, username));
    }
}
