package service;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    public static final CreateGameRequest GAME_REQUEST_1 = new CreateGameRequest("game1");
    private JoinGameRequest joinRequest1;

    @AfterEach
    void cleanup() {
        GameService.getInstance().clear();
    }

    @Test
    void getInstance() {
        assertNotNull(GameService.getInstance());
    }

    @Test
    void clear() {
        create();
        GameService.getInstance().clear();
        assertEquals(400, GameService.getInstance().join(joinRequest1, "user").statusCode());
    }

    @Test
    void getGames() {
        create();
        ServiceResponse res = GameService.getInstance().getGames();
        assertEquals(200, res.statusCode());
        GamesResponse data = ((GamesResponse) res.data());
        assertEquals(1, data.games().size());
    }

    @Test
    void create() {
        ServiceResponse res = GameService.getInstance().create(GAME_REQUEST_1);
        assertEquals(200, res.statusCode());
        int game1ID = ((GameResponse) res.data()).gameID();
        joinRequest1 = new JoinGameRequest(ChessGame.TeamColor.WHITE, game1ID);
    }

    @Test
    void failCreate() {
        create();
        assertEquals(400, GameService.getInstance().create(new CreateGameRequest(null)).statusCode());
    }

    @Test
    void join() {
        create();
        assertEquals(200, GameService.getInstance().join(joinRequest1, "user").statusCode());
    }

    @Test
    void failJoin() {
        create();
        clear();
        assertEquals(400, GameService.getInstance().join(joinRequest1, "user").statusCode());
    }
}