package ui;

import connection.ServerFacade;
import connection.ServerResponse;
import model.GameResponse;

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

    public PostLoginUI(Scanner scanner, String username, String authToken) {
        super(scanner, "logout");
        this.username = username;
        this.authToken = authToken;
    }

    @Override
    protected void printHelp() {
        System.out.println("post login help!");
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
}
