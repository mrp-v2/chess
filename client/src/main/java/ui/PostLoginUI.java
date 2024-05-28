package ui;

import java.util.Scanner;

public class PostLoginUI extends UserInputHandler {
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

    }
}
