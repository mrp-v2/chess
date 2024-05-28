import chess.*;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command;
        do {
            command = scanner.nextLine();
            String[] parts = command.split(" ");
            if (parts.length == 0) {
                printHelp();
                continue;
            }
            switch (parts[0]) {
                case "quit":
                    break;
                case "login":
                    login(Arrays.copyOfRange(parts, 1, parts.length));
                    break;
                case "register":
                    register(Arrays.copyOfRange(parts, 1, parts.length));
                    break;
                default:
                    printHelp();
                    break;
            }
        } while (!"quit".equals(command));
    }

    private static void printHelp() {
        System.out.println("Help!");
    }

    private static void login(String[] args) {
        if (args.length != 2){
            printHelp();
            return;
        }
        // TODO server facade login
    }

    private static void register(String[] args) {
        if (args.length != 3){
            printHelp();
            return;
        }
        // TODO server facade register
    }
}