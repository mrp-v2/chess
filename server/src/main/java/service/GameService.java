package service;

import dataAccess.DataAccessException;
import dataAccess.GameAccess;
import model.*;

public class GameService {

    private static GameService instance;

    public static GameService getInstance() {
        if (instance == null) {
            instance = new GameService();
        }
        return instance;
    }

    public void clear() {
        GameAccess.Local.getInstance().clear();
    }

    public ServiceResponse getGames() {
        return Wrapper.success(new GamesResponse(GameAccess.Local.getInstance().getGames()));
    }

    public ServiceResponse create(GameRequest data) {
        if (data.gameName() == null) {
            return ErrorModel.BAD_REQUEST;
        }
        GameData result = GameAccess.Local.getInstance().createGame(data.gameName());
        return Wrapper.success(new GameResponse(result.gameID()));
    }

    public ServiceResponse join(JoinGameRequest data, String username) {
        // verify the game exists and the request is valid
        GameData result = GameAccess.Local.getInstance().getGame(data.gameID());
        if (result == null || data.playerColor() == null) {
            return ErrorModel.BAD_REQUEST;
        }
        GameData modified = null;
        // add player to correct color
        switch (data.playerColor()) {
            case WHITE:
                if (result.whiteUsername() == null) {
                    modified = result.addWhiteUser(username);
                }
                break;
            case BLACK:
                if (result.blackUsername() == null) {
                    modified = result.addBlackUser(username);
                }
                break;
        }
        // if color was taken send an error response
        if (modified == null) {
            return ErrorModel.ALREADY_TAKEN;
        }
        // try to update the game, should succeed because the gameID is checked earlier
        try {
            GameAccess.Local.getInstance().updateGame(result.gameID(), modified);
            return ServiceResponse.SUCCESS;
        } catch (DataAccessException e) {
            throw new RuntimeException("Tried to update a game that didn't exist, but this should have already been verified.");
        }
    }
}
