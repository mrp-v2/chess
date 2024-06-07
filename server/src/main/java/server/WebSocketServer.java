package server;

import chess.ChessGame;
import chess.InvalidMoveException;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.AuthService;
import service.GameService;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

@WebSocket
public class WebSocketServer {

    private final Map<Integer, ActiveGame> activeGames;
    private final Map<Session, Runnable> connectionClosedHandles;

    public WebSocketServer() {
        activeGames = new HashMap<>();
        connectionClosedHandles = new HashMap<>();
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
        ActiveGame activeGame = activeGames.computeIfAbsent(gameData.gameID(), key -> {
            return new ActiveGame();
        });
        connectionClosedHandles.put(session, activeGame.addUser(username, gameData.getPlayerColor(username)));
        sendData(session, new GameMessage(gameData));
    }

    private void handleMakeMove(Session session, String username, GameData gameData, MoveCommand command) {
        try {
            if (gameData.game().getTeamTurn() == null) {
                sendError(session, "can't make move after game is over");
                return;
            }
            ChessGame.TeamColor moveColor = gameData.game().getBoard().getPiece(command.move.getStartPosition()).getTeamColor();
            if (moveColor != getPlayerColor(username, gameData)) {
                sendError(session, "can't make move for another player");
                return;
            }
            gameData.game().makeMove(command.move);
            ServiceResponse response = GameService.getInstance().update(gameData);
            if (response.failure()) {
                sendError(session, response.toJson());
                return;
            }
            ActiveGame activeGame = activeGames.get(gameData.gameID());
            activeGame.notifyUsers(new GameMessage(gameData));
            activeGame.notifyOtherUsers(username, new NotificationMessage(String.format("%s moved %s to %s", username, command.move.getStartPosition(), command.move.getEndPosition())));
            if (gameData.game().getWinner() != null) {
                activeGame.notifyUsers(new NotificationMessage(String.format("%s wins by checkmate", gameData.game().getWinner())));
            } else if (gameData.game().isInCheck(gameData.game().getTeamTurn())) {
                activeGame.notifyUsers(new NotificationMessage(String.format("%s is in check", gameData.game().getTeamTurn())));
            }
        } catch (InvalidMoveException e) {
            sendError(session, "invalid move");
        }
    }

    private void handleLeave(Session session, String username, GameData gameData, UserGameCommand command) {
        ActiveGame activeGame = activeGames.get(gameData.gameID());
        if (username.equals(gameData.whiteUsername())) {
            activeGame.removeWhite();
        } else if (username.equals(gameData.blackUsername())) {
            activeGame.removeBlack();
        } else {
            activeGame.removeObserver(username);
        }
        if (!activeGame.isActive()) {
            activeGames.remove(gameData.gameID());
        }
        connectionClosedHandles.remove(session);
        ServiceResponse response = GameService.getInstance().leave(gameData, username);
        if (response.failure()) {
            sendError(session, response.toJson());
        }
    }

    private void handleResign(Session session, String username, GameData gameData, UserGameCommand command) {
        if (gameData.game().getTeamTurn() == null) {
            sendError(session, "game is already over");
            return;
        }
        if (getPlayerColor(username, gameData) == null) {
            sendError(session, "cannot resign without being a player");
            return;
        }
        gameData.game().resign(getPlayerColor(username, gameData));
        ServiceResponse response = GameService.getInstance().update(gameData);
        if (response.failure()) {
            sendError(session, response.toJson());
        }
        activeGames.get(gameData.gameID()).notifyUsers(new NotificationMessage(String.format("%s resigned", username)));
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
            session.getRemote().sendString(new ErrorMessage(error).toJson());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ChessGame.TeamColor getPlayerColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return WHITE;
        } else if (username.equals(game.blackUsername())) {
            return BLACK;
        } else {
            return null;
        }
    }

    @OnWebSocketClose
    public void onConnectionClosed(Session session, int statusCode, String reason) {
        Runnable run = connectionClosedHandles.get(session);
        if (run != null) {
            run.run();
            connectionClosedHandles.remove(session);
        }
    }

    @FunctionalInterface
    private interface UserAndGame<T extends UserGameCommand> {
        void run(Session session, String username, GameData gameData, T command);
    }
}
