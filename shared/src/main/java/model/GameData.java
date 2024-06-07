package model;

import chess.ChessGame;

import java.util.EnumSet;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName,
                       ChessGame game) implements JsonSerializable {

    public GameData setWhiteUser(String user) {
        return new GameData(gameID, user, blackUsername, gameName, game);
    }

    public GameData setBlackUser(String user) {
        return new GameData(gameID, whiteUsername, user, gameName, game);
    }

    public EnumSet<ChessGame.TeamColor> getPlayerColor(String user) {
        if (user.equals(whiteUsername) && user.equals(blackUsername)) {
            return EnumSet.of(ChessGame.TeamColor.WHITE, ChessGame.TeamColor.BLACK);
        } else if (user.equals(whiteUsername)) {
            return EnumSet.of(ChessGame.TeamColor.WHITE);
        } else if (user.equals(blackUsername)) {
            return EnumSet.of(ChessGame.TeamColor.BLACK);
        } else {
            return EnumSet.noneOf(ChessGame.TeamColor.class);
        }
    }
}
