package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import connection.ServerFacade;
import connection.WebSocketFacade;
import model.AuthResponse;
import model.GameData;

import java.util.Scanner;

public class GameplayUI extends PostLoginUI {

    private static final String HELP = """
                                       redraw
                                       leave
                                       move <position> <position>
                                       resign
                                       moves <position>
                                       help""";

    protected final WebSocketFacade socketFacade;
    protected final ChessGame.TeamColor playerColor;
    protected GameData game;

    public GameplayUI(Scanner scanner, GameData game, ChessGame.TeamColor playerColor, AuthResponse auth, ServerFacade serverFacade) {
        super(scanner, "exit", auth, serverFacade);
        this.game = game;
        this.playerColor = playerColor;
        this.socketFacade = new WebSocketFacade(serverFacade.getPort(), this, auth.authToken(), game.gameID());
    }

    @Override
    protected boolean handleArgs(String[] args) {
        switch (args[0]) {
            case "redraw":
                printBoard();
                break;
            case "leave":
                // exit game - color loses its user
                socketFacade.leave(auth.authToken(), game.gameID());
                return false;
            case "move":
                if (args.length != 3) {
                    printHelp();
                    break;
                }
                handleMove(args[1], args[2]);
                break;
            case "resign":
                handleResign();
                break;
            case "moves":
                // show the legal moves for a piece
                break;
            case "help":
                printHelp();
                break;
        }
        return true;
    }

    public void printBoard() {
        printInterrupt(this::printBoardInterrupt);
    }

    @Override
    protected void printHelp() {
        System.out.println(HELP);
    }

    private void handleMove(String from, String to) {
        if (from.length() != 2) {
            System.out.printf("invalid position '%s', should be two characters\n", from);
            return;
        }
        if (to.length() != 2) {
            System.out.printf("invalid position '%s', should be two characters\n", to);
            return;
        }
        ChessPosition fromPos = new ChessPosition(from);
        if (!fromPos.isValid()) {
            System.out.printf("invalid position '%s'\n", from);
            return;
        }
        ChessPosition toPos = new ChessPosition(to);
        if (!toPos.isValid()) {
            System.out.printf("Invalid position '%s'\n", to);
            return;
        }
        socketFacade.move(auth.authToken(), game.gameID(), new ChessMove(fromPos, toPos));
    }

    private void handleResign() {
        ResignUI ui = new ResignUI(scanner, serverFacade);
    }

    private void printBoardInterrupt() {
        PrintBoardHelper.printBoard(game.game().getBoard(), playerColor);
    }

    protected void doResign() {
        socketFacade.resign(auth.authToken(), game.gameID());
    }

    public void updateBoard(GameData game) {
        this.game = game;
        printBoard();
    }

    public void notification(String message) {
        printInterrupt(message);
    }

    private class ResignUI extends UserInputHandler {
        protected ResignUI(Scanner scanner, ServerFacade serverFacade) {
            super(scanner, "", serverFacade);
        }

        @Override
        protected void printHelp() {
            System.out.println("are you sure you want to resign? (y): ");
        }

        @Override
        protected boolean handleArgs(String[] args) {
            if (args.length == 1 && args[0].equals("y")) {
                doResign();
            }
            return false;
        }
    }
}
