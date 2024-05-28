package ui;

import chess.ChessGame;
import connection.ServerFacade;
import connection.ServerResponse;
import model.GameData;
import model.GameResponse;
import model.GamesResponse;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI extends UserInputHandler {

    private static final String HELP = """
                                       logout
                                       help
                                       create <game name>
                                       list
                                       join <game number> <WHITE|BLACK>
                                       observe <game number>""";

    private final String username;
    private final String authToken;

    private GameData[] games;

    public PostLoginUI(Scanner scanner, String username, String authToken) {
        super(scanner, "logout");
        this.username = username;
        this.authToken = authToken;
    }

    @Override
    protected void printHelp() {
        System.out.println(HELP);
    }

    @Override
    protected void handleArgs(String[] args) {
        switch (args[0]) {
            case "create":
                createGame(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "list":
                break;
            case "join":
                break;
            case "observe":
                break;
            default:
                printHelp();
                break;
        }
    }

    private void createGame(String[] args) {
        if (args.length != 1) {
            printHelp();
            return;
        }
        ServerResponse<GameResponse> response = ServerFacade.createGame(args[0], username, authToken);
        if (!response.ok()) {
            printError(response);
        }
    }

    private void listGames(String[] args) {
        if (args.length != 0) {
            printHelp();
            return;
        }
        ServerResponse<GamesResponse> response = ServerFacade.getGames(username, authToken);
        if (!response.ok()) {
            printError(response);
            return;
        }
        games = response.data().games().toArray(new GameData[0]);
        for (int i = 0; i < games.length; i++) {
            System.out.printf("%d: Name=%s White=%s Black=%s", i, games[i].gameName(), games[i].whiteUsername(), games[i].blackUsername());
        }
    }

    private void joinGame(String[] args) {
        if (args.length != 2) {
            printHelp();
            return;
        }
        int gameIndex;
        ChessGame.TeamColor color;
        try {
            gameIndex = Integer.parseInt(args[0]);
            color = ChessGame.TeamColor.valueOf(args[1]);
        } catch (NumberFormatException e) {
            System.out.printf("Invalid argument '%s': should be an integer", args[0]);
            return;
        } catch (IllegalArgumentException e) {
            System.out.printf("Invalid argument '%s': should be 'white' or 'black'", args[1]);
            return;
        }
        if (gameIndex < 0 || gameIndex >= games.length) {
            System.out.printf("Game index %d is out of range. Should be between 0 and %d, inclusive", gameIndex, games.length - 1);
            return;
        }
        ServerResponse<?> response = ServerFacade.joinGame(username, authToken, games[gameIndex], color);
        if (!response.ok()) {
            printError(response);
        }
    }

    private void observeGame(String[] args) {
        if (args.length != 1) {
            printHelp();
            return;
        }
        int gameIndex;
        try {
            gameIndex = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.printf("Invalid argument '%s': should be an integer", args[0]);
            return;
        }
        if (gameIndex < 0 || gameIndex >= games.length) {
            System.out.printf("Game index %d is out of range. Should be between 0 and %d, inclusive", gameIndex, games.length - 1);
            return;
        }
        ServerResponse<?> response = ServerFacade.observeGame(username, authToken, games[gameIndex]);
    }
}
