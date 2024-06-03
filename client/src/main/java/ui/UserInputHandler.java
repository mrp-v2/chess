package ui;

import connection.ServerFacade;
import connection.ServerResponse;

import java.util.Scanner;

public abstract class UserInputHandler {

    protected final Scanner scanner;
    protected final ServerFacade serverFacade;
    private final String exitCommand;

    protected UserInputHandler(Scanner scanner, String exitCommand, ServerFacade serverFacade) {
        this.scanner = scanner;
        this.exitCommand = exitCommand;
        this.serverFacade = serverFacade;
    }

    public void run() {
        printHelp();
        String command;
        boolean again = true;
        while (again) {
            System.out.print("> ");
            command = scanner.nextLine();
            if (exitCommand.equals(command)) {
                break;
            } else {
                String[] args = command.split(" ");
                if (args.length != 0) {
                    again = handleArgs(args);
                }
            }
        }
    }

    protected abstract void printHelp();

    protected abstract boolean handleArgs(String[] args);

    protected void printError(ServerResponse<?> response) {
        if (response.responseCode() == 0) {
            System.out.println(response.responseMessage());
        } else {
            System.out.printf("Error %d: %s%n", response.responseCode(), response.responseMessage());
        }
    }
}
