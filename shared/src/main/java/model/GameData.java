package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName,
                       ChessGame game) implements JsonSerializable {

    public GameData setWhiteUser(String user) {
        return new GameData(gameID, user, blackUsername, gameName, game);
    }

    public GameData setBlackUser(String user) {
        return new GameData(gameID, whiteUsername, user, gameName, game);
    }
}
