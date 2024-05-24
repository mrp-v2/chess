package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameAccessTest extends SQLAccessTest {

    private static final String NAME = "game name";
    private static final ChessGame GAME = new ChessGame();
    private int gameId;

    @Test
    void getInstance() {
        assertNotNull(SQLGameAccess.getInstance());
    }

    @Test
    void getGames() {
        noError(() -> {
            assertEquals(0, SQLGameAccess.getInstance().getGames().size());
        });
        createGame();
        createGame();
        noError(() -> {
            assertEquals(2, SQLGameAccess.getInstance().getGames().size());
        });
    }

    @Test
    void createGame() {
        noError(() -> {
            gameId = SQLGameAccess.getInstance().createGame(NAME, GAME);
            assertTrue(gameId > 0);
        });
    }

    @Test
    void getGameData() {
        createGame();
        noError(() -> {
            GameData data = SQLGameAccess.getInstance().getGameData(gameId);
            assertNotNull(data);
        });
    }

    @Test
    void getGameDataFails() {
        noError(() -> {
            GameData data = SQLGameAccess.getInstance().getGameData(gameId);
            assertNull(data);
        });
    }

    @Test
    void updateGame() {
        createGame();
        noError(() -> {
            GameData updated = new GameData(gameId, "white", "black", "hehe", new ChessGame());
            SQLGameAccess.getInstance().updateGame(gameId, updated);
            GameData observed = SQLGameAccess.getInstance().getGameData(gameId);
            assertEquals(updated, observed);
        });
    }

    @Test
    void updateGameFails() {
        assertThrows(DataAccessException.class, () -> {
            SQLGameAccess.getInstance().updateGame(0, new GameData(0, "", "", "", new ChessGame()));
        });
    }

    @BeforeEach
    @AfterEach
    @Test
    void clearNoError() {
        noError(() -> {
            SQLGameAccess.getInstance().clear();
        });
    }

    @Test
    void clear() {
        createGame();
        clearNoError();
        getGameDataFails();
    }
}