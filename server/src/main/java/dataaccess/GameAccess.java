package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface GameAccess {

    Collection<GameData> getGames();

    GameData createGame(String gameName);

    GameData getGame(int gameID);

    /**
     * @param gameID The ID of the game to update.
     * @param gameData The new data for the game.
     * @throws DataAccessException - the {@code gameID} is invalid.
     */
    void updateGame(int gameID, GameData gameData) throws DataAccessException;

    /**
     * Deletes all games.
     */
    void clear();

    /**
     * Implements {@link GameAccess} using RAM.
     */
    class Local implements GameAccess {

        private static GameAccess instance;

        private final Map<Integer, GameData> games;
        private int currentID;

        private Local() {
            games = new HashMap<>();
            currentID = 1;
        }

        /**
         * Returns a reference to the global {@link Local} instance.
         */
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
        public void updateGame(int gameID, GameData gameData) throws DataAccessException {
            if (!games.containsKey(gameID)) {
                throw new DataAccessException("invalid gameID");
            }
            games.put(gameID, gameData);
        }

        @Override
        public void clear() {
            games.clear();
            currentID = 1;
        }
    }
}
