package client;

import chess.ChessGame;
import connection.ServerFacade;
import connection.ServerResponse;
import model.AuthResponse;
import model.GameResponse;
import model.GamesResponse;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String GAME_NAME = "game";
    private static final String INVALID = "blah blah";
    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerFail() {
        register();
        ServerResponse<AuthResponse> response = facade.register(USERNAME, PASSWORD, EMAIL);
        assertFalse(response.ok());
    }

    @Test
    void register() {
        ServerResponse<AuthResponse> response = facade.register(USERNAME, PASSWORD, EMAIL);
        assertTrue(response.ok());
    }

    @Test
    void login() {
        register();
        ServerResponse<AuthResponse> response = facade.login(USERNAME, PASSWORD);
        assertTrue(response.ok());
    }

    @Test
    void loginFail() {
        ServerResponse<AuthResponse> response = facade.login(USERNAME, PASSWORD);
        assertFalse(response.ok());
    }

    @Test
    void createGame() {
        String authToken = facade.register(USERNAME, PASSWORD, EMAIL).data().authToken();
        ServerResponse<GameResponse> response = facade.createGame(authToken, GAME_NAME);
        assertTrue(response.ok());
    }

    @Test
    void createGameFail() {
        assertFalse(facade.createGame(INVALID, GAME_NAME).ok());
    }

    @Test
    void getGames() {
        String authToken = facade.register(USERNAME, PASSWORD, EMAIL).data().authToken();
        ServerResponse<GamesResponse> response = facade.getGames(authToken);
        assertTrue(response.ok());
    }

    @Test
    void getGamesFail() {
        assertFalse(facade.getGames(INVALID).ok());
    }

    @Test
    void joinGame() {
        String authToken = facade.register(USERNAME, PASSWORD, EMAIL).data().authToken();
        int gameID = facade.createGame(authToken, GAME_NAME).data().gameID();
        ServerResponse<?> response = facade.joinGame(authToken, gameID, ChessGame.TeamColor.WHITE);
        assertTrue(response.ok());
    }

    @Test
    void joinGameFailNoAuth() {
        assertFalse(facade.joinGame(INVALID, 0, ChessGame.TeamColor.WHITE).ok());
    }

    @Test
    void joinGameFailColorTaken() {
        String authToken = facade.register(USERNAME, PASSWORD, EMAIL).data().authToken();
        int gameID = facade.createGame(authToken, GAME_NAME).data().gameID();
        assertTrue(facade.joinGame(authToken, gameID, ChessGame.TeamColor.WHITE).ok());
        assertFalse(facade.joinGame(authToken, gameID, ChessGame.TeamColor.WHITE).ok());
    }

    @Test
    void joinGameFailInvalidGame() {
        String authToken = facade.register(USERNAME, PASSWORD, EMAIL).data().authToken();
        assertFalse(facade.joinGame(authToken, -1, ChessGame.TeamColor.WHITE).ok());
    }

    @Test
    void observeGame() {
        // TODO
        assertTrue(true);
    }

    @Test
    @BeforeEach
    @AfterEach
    void clearNoError() {
        ServerResponse<?> response = facade.clear();
        assertTrue(response.ok());
    }
}
