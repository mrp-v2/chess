package model;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import server.WebSocketServer;
import websocket.messages.MessageMessage;

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

    public void addWhite(Session white, String username) {
        this.white = new SessionInfo(white, username);
        MessageMessage message = MessageMessage.joinNotification(username, ChessGame.TeamColor.WHITE);
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

    public void addBlack(Session black, String username) {
        this.black = new SessionInfo(black, username);
        MessageMessage message = MessageMessage.joinNotification(username, ChessGame.TeamColor.BLACK);
        if (white != null) {
            WebSocketServer.sendData(white.session(), message);
        }
        sendToObservers(message);
    }

    public void addObserver(Session observer, String username) {
        observers.add(new SessionInfo(observer, username));
    }

    public boolean isActive() {
        return white != null || black != null || !observers.isEmpty();
    }
}
