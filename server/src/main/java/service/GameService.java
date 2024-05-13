package service;

import dataAccess.GameAccess;
import model.*;
import spark.Request;
import spark.Response;

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

    public IServiceResponse getGames() {
        return new GamesData(GameAccess.Local.getInstance().getGames());
    }

    public IServiceResponse create(GameRequest data) {
        GameData result = GameAccess.Local.getInstance().createGame(data.gameName());
        return new GameResponse(result.gameID());
    }

    public IServiceResponse join(JoinGameRequest data, String username) {
        GameData result = GameAccess.Local.getInstance().getGame(data.gameID());
        if (result == null) {
            return ErrorModel.BAD_REQUEST;
        }
        switch (data.playerColor()) {
            case WHITE:
                if (result.whiteUsername() == null) {
                    GameAccess.Local.getInstance().updateGame(result.gameID(), result.addWhiteUser(username));
                    return IResponseModel.SUCCESS;
                }
                break;
            case BLACK:
                if (result.blackUsername() == null) {
                    GameAccess.Local.getInstance().updateGame(result.gameID(), result.addBlackUser(username));
                    return IResponseModel.SUCCESS;
                }
                break;
        }
        return ErrorModel.ALREADY_TAKEN;
    }
}
