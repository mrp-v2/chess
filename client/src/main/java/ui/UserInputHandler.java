package ui;

import connection.ServerResponse;

import java.util.Scanner;

public abstract class UserInputHandler {

    protected final Scanner scanner;
    private final String exitCommand;

    protected UserInputHandler(Scanner scanner, String exitCommand) {
        this.scanner = scanner;
        this.exitCommand = exitCommand;
    }

    public void run() {
        printHelp();
        String command;
        while (true) {
            command = scanner.nextLine();
            if (exitCommand.equals(command)) {
                break;
            } else {
                String[] args = command.split(" ");
                if (args.length == 0) {
                    printHelp();
                } else {
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
