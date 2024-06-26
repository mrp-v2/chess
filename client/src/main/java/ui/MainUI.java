package ui;

import connection.ServerFacade;
import connection.ServerResponse;
import model.AuthResponse;

import java.util.Arrays;
import java.util.Scanner;

public class MainUI extends UserInputHandler {

    private static final String HELP = """
                                       quit
                                       help
                                       login <username> <password>
                                       register <username> <password> <email>""";

    public MainUI(Scanner scanner, ServerFacade serverFacade) {
        super(scanner, "quit", serverFacade);
    }

    @Override
    protected void printHelp() {
        System.out.println(HELP);
    }

    @Override
    protected boolean handleArgs(String[] args) {
        switch (args[0]) {
            case "login":
                login(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "register":
                register(Arrays.copyOfRange(args, 1, args.length));
                break;
            default:
                printHelp();
                break;
        }
        return true;
    }

    private void login(String[] args) {
        if (args.length != 2) {
            printHelp();
            return;
        }
        ServerResponse<AuthResponse> response = serverFacade.login(args[0], args[1]);
        handleAuthResponse(response);
    }

    private void register(String[] args) {
        if (args.length != 3) {
            printHelp();
            return;
        }
        ServerResponse<AuthResponse> response = serverFacade.register(args[0], args[1], args[2]);
        handleAuthResponse(response);
    }

    private void handleAuthResponse(ServerResponse<AuthResponse> response) {
        if (response.ok()) {
            PostLoginUI postLoginUI = new PostLoginUI(scanner, response.data(), serverFacade);
            System.out.println("Success!");
            postLoginUI.run();
            printHelp();
        } else {
            printError(response);
        }
    }
}
