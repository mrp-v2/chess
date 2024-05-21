package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName,
                       ChessGame game) {

    public GameData addWhiteUser(String user) {
        return new GameData(gameID, user, blackUsername, gameName, game);
    }

    public GameData addBlackUser(String user) {
        return new GameData(gameID, whiteUsername, user, gameName, game);
    }
}
