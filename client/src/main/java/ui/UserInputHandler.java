package ui;

import connection.ServerFacade;
import connection.ServerResponse;

import java.util.Scanner;

public abstract class UserInputHandler {

    protected final Scanner scanner;
    private final String exitCommand;
    protected final ServerFacade serverFacade;

    protected UserInputHandler(Scanner scanner, String exitCommand, ServerFacade serverFacade) {
        this.scanner = scanner;
        this.exitCommand = exitCommand;
        this.serverFacade = serverFacade;
    }

    public void run() {
        printHelp();
        String command;
        while (true) {
            System.out.print("> ");
            command = scanner.nextLine();
            if (exitCommand.equals(command)) {
                break;
            } else {
                String[] args = command.split(" ");
                if (args.length != 0) {
                    handleArgs(args);
                }
            }
        }
    }

    protected abstract void printHelp();

    protected abstract void handleArgs(String[] args);

    protected void printError(ServerResponse<?> response) {
        if (response.responseCode() == 0) {
            System.out.println(response.responseMessage());
        } else {
            System.out.printf("Error %d: %s%n", response.responseCode(), response.responseMessage());
        }
    }
}
