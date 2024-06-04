package model;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import server.WebSocketServer;
import websocket.messages.NotificationMessage;

import java.util.ArrayList;
import java.util.List;

public class ActiveGame {
    private final List<SessionInfo> observers;
    private SessionInfo white;
    private SessionInfo black;

    public ActiveGame() {
        white = null;
        black = null;
        observers = new ArrayList<>();
    }

    public Runnable addWhite(Session white, String username) {
        this.white = new SessionInfo(white, username);
        NotificationMessage message = NotificationMessage.joinNotification(username, ChessGame.TeamColor.WHITE);
        notifyOtherUsers(username, message);
        return () -> {
            this.white = null;
            notifyUsersOfLostConnection(username);
        };
    }

    private void notifyOtherUsers(String username, NotificationMessage data) {
        if (white != null && !white.username().equals(username)) {
            WebSocketServer.sendData(white.session(), data);
        }
        if (black != null && !black.username().equals(username)) {
            WebSocketServer.sendData(black.session(), data);
        }
        for (SessionInfo info : observers) {
            if (!info.username().equals(username)) {
                WebSocketServer.sendData(info.session(), data);
            }
        }
    }

    private void notifyUsersOfLostConnection(String username) {
        notifyUsers(new NotificationMessage(String.format("%s lost their connection", username)));
    }

    private void notifyUsers(NotificationMessage message) {
        if (white != null) {
            WebSocketServer.sendData(white.session(), message);
        }
        if (black != null) {
            WebSocketServer.sendData(black.session(), message);
        }
        sendToObservers(message);
    }

    private void sendToObservers(JsonSerializable data) {
        for (SessionInfo info : observers) {
            WebSocketServer.sendData(info.session(), data);
        }
    }

    public Runnable addBlack(Session black, String username) {
        this.black = new SessionInfo(black, username);
        NotificationMessage message = NotificationMessage.joinNotification(username, ChessGame.TeamColor.BLACK);
        notifyOtherUsers(username, message);
        return () -> {
            this.black = null;
            notifyUsersOfLostConnection(username);
        };
    }

    public Runnable addObserver(Session observer, String username) {
        observers.add(new SessionInfo(observer, username));
        NotificationMessage message = new NotificationMessage(String.format("%s started observing the game", username));
        notifyOtherUsers(username, message);
        return () -> {
            observers.removeIf(info -> {
                return info.username().equals(username);
            });
            removeObserver(username);
        };
    }

    public void removeObserver(String username) {
        observers.removeIf(info -> {
            return info.username().equals(username);
        });
        notifyUsersOfLeaving(username);
    }

    private void notifyUsersOfLeaving(String username) {
        notifyOtherUsers(username, new NotificationMessage(String.format("%s left the game", username)));
    }

    public boolean isActive() {
        return white != null || black != null || !observers.isEmpty();
    }

    public void removeWhite() {
        notifyUsersOfLeaving(white.username());
        white = null;
    }

    public void removeBlack() {
        notifyUsersOfLeaving(black.username());
        black = null;
    }
}
