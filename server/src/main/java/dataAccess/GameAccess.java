package dataAccess;

import chess.ChessGame;
import model.GameData;
import model.GamesData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface GameAccess {

    Collection<GameData> getGames();

    GameData createGame(String gameName);

    GameData getGame(int gameID);

    void updateGame(int gameID, GameData gameData);

    void clear();

    class Local implements GameAccess {

        private static GameAccess instance;

        private final Map<Integer, GameData> games;
        private int currentID;

        public Local() {
            games = new HashMap<>();
            currentID = 0;
        }

        public static GameAccess getInstance() {
            if (instance == null) {
                instance = new Local();
            }
            return instance;
        }

        @Override
        public Collection<GameData> getGames() {
            return games.values();
        }

        @Override
        public GameData createGame(String gameName) {
            GameData data = new GameData(currentID, null, null, gameName, new ChessGame());
            games.put(currentID++, data);
            return data;
        }

        @Override
        public GameData getGame(int gameID) {
            return games.get(gameID);
        }

        @Override
        public void updateGame(int gameID, GameData gameData) {
            games.put(gameID, gameData);
        }

        @Override
        public void clear() {
            games.clear();
            currentID = 0;
        }
    }
}
