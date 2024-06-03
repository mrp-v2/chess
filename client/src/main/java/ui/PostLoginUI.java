package ui;

import chess.ChessGame;
import connection.ServerFacade;
import connection.ServerResponse;
import model.AuthResponse;
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

    private final AuthResponse auth;

    private GameData[] games;

    public PostLoginUI(Scanner scanner, AuthResponse auth, ServerFacade serverFacade) {
        super(scanner, "logout", serverFacade);
        this.auth = auth;
        this.games = new GameData[0];
    }

    @Override
    protected void handleArgs(String[] args) {
        switch (args[0]) {
            case "create":
                createGame(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "list":
                listGames(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "join":
                joinGame(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "observe":
                observeGame(Arrays.copyOfRange(args, 1, args.length));
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
        ServerResponse<GameResponse> response = serverFacade.createGame(auth.authToken(), args[0]);
        if (!response.ok()) {
            printError(response);
        } else {
            System.out.println("Success!");
        }
    }

    private void listGames(String[] args) {
        if (args.length != 0) {
            printHelp();
            return;
        }
        ServerResponse<GamesResponse> response = serverFacade.getGames(auth.authToken());
        if (!response.ok()) {
            printError(response);
            return;
        }
        games = response.data().games().toArray(new GameData[0]);
        if (games.length == 0) {
            System.out.println("No games");
        } else {
            for (int i = 0; i < games.length; i++) {
                System.out.printf("%d: Name=%s White=%s Black=%s\n", i, games[i].gameName(), games[i].whiteUsername(), games[i].blackUsername());
            }
        }
    }

    private void joinGame(String[] args) {
        if (args.length != 2) {
            printHelp();
            return;
        }
        if (!verifyGameCount()) {
            return;
        }
        int gameIndex = verifyGameIndex(args[0]);
        ChessGame.TeamColor color = verifyTeamColor(args[1]);
        if (gameIndex < 0 || color == null) {
            return;
        }
        GameData game = games[gameIndex];
        boolean serverJoin = true;
        switch (color) {
            case WHITE:
                if (auth.username().equals(game.whiteUsername())) {
                    serverJoin = false;
                }
                break;
            case BLACK:
                if (auth.username().equals(game.blackUsername())) {
                    serverJoin = false;
                }
        }
        GameplayUI gameplay;
        if (serverJoin) {
            ServerResponse<GameData> response = serverFacade.joinGame(auth.authToken(), game.gameID(), color);
            if (!response.ok()) {
                printError(response);
                return;
            }
            gameplay = new GameplayUI(scanner, response.data(), color, auth, serverFacade);
        } else {
            gameplay = new GameplayUI(scanner, game, color, auth, serverFacade);
        }
        gameplay.run();
    }

    private void observeGame(String[] args) {
        if (args.length != 1) {
            printHelp();
            return;
        }
        if (!verifyGameCount()) {
            return;
        }
        int gameIndex = verifyGameIndex(args[0]);
        if (gameIndex < 0) {
            return;
        }
        ServerResponse<?> response;
    }

    @Override
    protected void printHelp() {
        System.out.println(HELP);
    }

    private boolean verifyGameCount() {
        if (games.length == 0) {
            System.out.println("No games loaded. Try listing the games or creating a new one");
            return false;
        } else {
            return true;
        }
    }

    private int verifyGameIndex(String gameIndex) {
        try {
            int result = Integer.parseInt(gameIndex);
            if (result < 0) {
                System.out.printf("Invalid argument '%d': should be at least zero\n", result);
                return -1;
            }
            if (result >= games.length) {
                System.out.printf("Invalid argument '%d': should be less than %d\n", result, games.length);
                return -1;
            }
            return result;
        } catch (NumberFormatException e) {
            System.out.printf("Invalid argument '%s': should be an integer\n", gameIndex);
            return -1;
        }
    }

    private ChessGame.TeamColor verifyTeamColor(String teamColor) {
        try {
            return ChessGame.TeamColor.valueOf(teamColor.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.printf("Invalid argument '%s': should be 'white' or 'black'\n", teamColor);
            return null;
        }
    }
}
