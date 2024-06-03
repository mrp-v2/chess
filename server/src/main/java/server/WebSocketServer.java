package server;

import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.AuthService;
import service.GameService;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.GameMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class WebSocketServer {

    private final Map<Integer, ActiveGame> activeGames;

    public WebSocketServer() {
        activeGames = new HashMap<>();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = JsonSerializable.GSON.fromJson(message, UserGameCommand.class);
        switch (command.commandType) {
            case CONNECT:
                runWithUserAndGame(session, command, this::handleConnect);
                break;
            case MAKE_MOVE:
                runWithUserAndGame(session, (MoveCommand) command, this::handleMakeMove);
                break;
            case LEAVE:
                runWithUserAndGame(session, command, this::handleLeave);
                break;
            case RESIGN:
                runWithUserAndGame(session, command, this::handleResign);
                break;
        }
    }

    private <T extends UserGameCommand> void runWithUserAndGame(Session session, T command, UserAndGame<T> run) {
        String username = getUsername(session, command);
        if (username != null) {
            GameData gameData = getGameData(session, command);
            if (gameData != null) {
                run.run(session, username, gameData, command);
            }
        }
    }

    private void handleConnect(Session session, String username, GameData gameData, UserGameCommand command) {
        ActiveGame activeGame = activeGames.get(gameData.gameID());
        if (gameData.whiteUsername().equals(username)) {
            activeGame.addWhite(session, username);
        } else if (gameData.blackUsername().equals(username)) {
            activeGame.addBlack(session, username);
        } else {
            activeGame.addObserver(session, username);
        }
        sendData(session, new GameMessage(gameData));
    }

    private void handleMakeMove(Session session, String username, GameData gameData, MoveCommand command) {

    }

    private void handleLeave(Session session, String username, GameData gameData, UserGameCommand command) {
        ServiceResponse response = GameService.getInstance().leave(gameData, username);
        if (response.failure()) {
            sendError(session, response.toJson());
        }
    }

    private void handleResign(Session session, String username, GameData gameData, UserGameCommand command) {

    }

    private String getUsername(Session session, UserGameCommand command) {
        ServiceResponse response = AuthService.getInstance().validate(command.authToken);
        if (response.failure()) {
            sendError(session, response.toJson());
            return null;
        }
        return ((UserResponse) response.data()).username();
    }

    private GameData getGameData(Session session, UserGameCommand command) {
        ServiceResponse response = GameService.getInstance().getGame(command.gameID);
        if (response.failure()) {
            sendError(session, response.toJson());
            return null;
        }
        return (GameData) response.data();
    }

    public static void sendData(Session session, JsonSerializable data) {
        try {
            session.getRemote().sendString(data.toJson());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendError(Session session, String error) {
        try {
            session.getRemote().sendString(error);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    private interface UserAndGame<T extends UserGameCommand> {
        void run(Session session, String username, GameData gameData, T command);
    }
}
