package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.JsonSerializable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameAccess extends SQLAccess implements GameAccess {

    private static SQLGameAccess instance;

    public static SQLGameAccess getInstance() {
        if (instance == null) {
            instance = new SQLGameAccess();
        }
        return instance;
    }

    protected SQLGameAccess() {
        super("""
              CREATE TABLE IF NOT EXISTS game (
                  `id` INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                  `usernameWhite` VARCHAR(256),
                  `usernameBlack` VARCHAR(256),
                  `name` VARCHAR(256) NOT NULL,
                  `game` TEXT NOT NULL
              );
              """);
    }

    @Override
    public Collection<GameData> getGames() throws DataAccessException {
        Collection<GameData> data = new ArrayList<>();
        query("SELECT * FROM game", result -> {
            do {
                data.add(this.resultToData(result));
            } while (result.next());
            return data;
        });
        return data;
    }

    @Override
    public int createGame(String name, ChessGame game) throws DataAccessException {
        return updateOne("INSERT INTO game (name, game) VALUES (?, ?)", statement -> {
            statement.setString(1, name);
            statement.setString(2, game.toJson());
        }, result -> {
            return result.getInt(1);
        });
    }

    @Override
    public GameData getGameData(int gameID) throws DataAccessException {
        return query("SELECT * FROM game WHERE id=?", statement -> {
            statement.setInt(1, gameID);
        }, this::resultToData);
    }

    private GameData resultToData(ResultSet result) throws SQLException {
        return new GameData(result.getInt("id"), result.getString("usernameWhite"), result.getString("usernameBlack"), result.getString("name"), JsonSerializable.GSON.fromJson(result.getString("game"), ChessGame.class));
    }

    @Override
    public void updateGame(int gameID, GameData data) throws DataAccessException {
        updateOne("UPDATE game SET usernameWhite=?, usernameBlack=?, name=?, game=? WHERE id=?", statement -> {
            statement.setString(1, data.whiteUsername());
            statement.setString(2, data.blackUsername());
            statement.setString(3, data.gameName());
            statement.setString(4, data.game().toJson());
            statement.setInt(5, gameID);
        });
    }

    @Override
    public void clear() throws DataAccessException {
        update("TRUNCATE game");
    }
}
