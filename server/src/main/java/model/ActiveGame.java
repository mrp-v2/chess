package model;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import server.WebSocketServer;
import websocket.messages.NotificationMessage;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ActiveGame {
    private final Map<String, SessionInfo> users;

    public ActiveGame() {
        users = new HashMap<>();
    }

    public void notifyUsers(JsonSerializable message) {
        for (SessionInfo info : users.values()) {
            WebSocketServer.sendData(info.session(), message);
        }
    }

    public Runnable addUser(Session session, String username, EnumSet<ChessGame.TeamColor> playerColor) {
        users.put(username, new SessionInfo(session, username, playerColor));
        String message;
        switch (playerColor.size()) {
            case 2:
                message = "%s joined as WHITE & BLACK";
                break;
            case 1:
                if (playerColor.contains(ChessGame.TeamColor.WHITE)) {
                    message = "%s joined as WHITE";
                } else {
                    message = "%s joined as BLACK";
                }
                break;
            default:
                message = "%s started observing the game";
        }
        NotificationMessage notify = new NotificationMessage(String.format(message, username));
        notifyOtherUsers(username, notify);
        return () -> {
            users.remove(username);
            notifyUsersOfLostConnection(username);
        };
    }

    public void notifyOtherUsers(String username, JsonSerializable data) {
        for (SessionInfo info : users.values()) {
            if (!info.username().equals(username)) {
                WebSocketServer.sendData(info.session(), data);
            }
        }
    }

    private void notifyUsersOfLostConnection(String username) {
        notifyOtherUsers(username, new NotificationMessage(String.format("%s lost their connection", username)));
    }

    public boolean isActive() {
        return !users.isEmpty();
    }

    public void removeUser(String username) {
        notifyUsersOfLeaving(username);
        users.remove(username);
    }

    private void notifyUsersOfLeaving(String username) {
        notifyOtherUsers(username, new NotificationMessage(String.format("%s left the game", username)));
    }
}
