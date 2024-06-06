package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameAccess;
import dataaccess.SQLGameAccess;
import model.*;

public class GameService {

    private static GameService instance;

    private final GameAccess gameAccess;

    private GameService() {
        gameAccess = SQLGameAccess.getInstance();
    }

    public static GameService getInstance() {
        if (instance == null) {
            instance = new GameService();
        }
        return instance;
    }

    public ServiceResponse clear() {
        try {
            gameAccess.clear();
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
        return ServiceResponse.SUCCESS;
    }

    public ServiceResponse getGames() {
        try {
            return Wrapper.success(new GamesResponse(gameAccess.getGames()));
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
    }

    public ServiceResponse create(CreateGameRequest data) {
        if (data.gameName() == null) {
            return ErrorModel.BAD_REQUEST;
        }
        int result;
        try {
            result = gameAccess.createGame(data.gameName(), new ChessGame());
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
        return Wrapper.success(new GameResponse(result));
    }

    public ServiceResponse getGame(int gameID) {
        GameData result;
        try {
            result = gameAccess.getGameData(gameID);
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
        if (result == null) {
            return ErrorModel.BAD_REQUEST;
        }
        return Wrapper.success(result);
    }

    public ServiceResponse leave(GameData game, String username) {
        GameData modified = null;
        if (username.equals(game.whiteUsername())) {
            modified = game.setWhiteUser(null);
        } else if (username.equals(game.blackUsername())) {
            modified = game.setBlackUser(null);
        }
        if (modified == null) {
            return ServiceResponse.SUCCESS;
        }
        try {
            gameAccess.updateGame(game.gameID(), modified);
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
        return ServiceResponse.SUCCESS;
    }

    public ServiceResponse update(GameData game) {
        GameData original;
        try {
            original = gameAccess.getGameData(game.gameID());
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
        if (original == null) {
            return ErrorModel.BAD_REQUEST;
        }
        try {
            gameAccess.updateGame(game.gameID(), game);
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
        return Wrapper.success(game);
    }

    public ServiceResponse join(JoinGameRequest data, String username) {
        // verify the game exists and the request is valid
        GameData result;
        try {
            result = gameAccess.getGameData(data.gameID());
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
        if (result == null || data.playerColor() == null) {
            return ErrorModel.BAD_REQUEST;
        }
        if (username.equals(result.blackUsername()) || username.equals(result.whiteUsername())) {
            return ErrorModel.ALREADY_JOINED;
        }
        GameData modified = null;
        // add player to correct color
        switch (data.playerColor()) {
            case WHITE:
                if (result.whiteUsername() == null) {
                    modified = result.setWhiteUser(username);
                }
                break;
            case BLACK:
                if (result.blackUsername() == null) {
                    modified = result.setBlackUser(username);
                }
                break;
        }
        // if color was taken send an error response
        if (modified == null) {
            return ErrorModel.ALREADY_TAKEN;
        }
        // try to update the game, should succeed because the gameID is checked earlier
        try {
            gameAccess.updateGame(result.gameID(), modified);
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
        return Wrapper.success(modified);
    }
}
