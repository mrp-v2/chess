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

    public IServiceResponse getGames() {
        return Wrapper.success(new GamesData(GameAccess.Local.getInstance().getGames()));
    }

    public IServiceResponse create(GameRequest data) {
        GameData result = GameAccess.Local.getInstance().createGame(data.gameName());
        return Wrapper.success(new GameResponse(result.gameID()));
    }

    public IServiceResponse join(JoinGameRequest data, String username) {
        GameData result = GameAccess.Local.getInstance().getGame(data.gameID());
        if (result == null || data.playerColor() == null) {
            return ErrorModel.BAD_REQUEST;
        }
        try {
            switch (data.playerColor()) {
                case WHITE:
                    if (result.whiteUsername() == null) {
                        GameAccess.Local.getInstance().updateGame(result.gameID(), result.addWhiteUser(username));
                        return IServiceResponse.SUCCESS;
                    }
                    break;
                case BLACK:
                    if (result.blackUsername() == null) {
                        GameAccess.Local.getInstance().updateGame(result.gameID(), result.addBlackUser(username));
                        return IServiceResponse.SUCCESS;
                    }
                    break;
            }
        } catch (DataAccessException e) {
            return ErrorModel.BAD_REQUEST;
        }
        return ErrorModel.ALREADY_TAKEN;
    }
}
