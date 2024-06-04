package ui;

import chess.ChessGame;
import connection.ServerFacade;
import connection.WebSocketFacade;
import model.AuthResponse;
import model.GameData;

import java.util.Scanner;

public class GameplayUI extends PostLoginUI {

    private final WebSocketFacade socketFacade;
    private final ChessGame.TeamColor playerColor;
    private GameData game;

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
                // make a move
                break;
            case "resign":
                // ask for confirmation before resigning, doesn't actually leave the game after resigning
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
        System.out.println("Gameplay help - coming soon");
    }

    private void printBoardInterrupt() {
        PrintBoardHelper.printBoard(game.game().getBoard(), playerColor);
    }

    public void updateBoard(GameData game) {
        this.game = game;
        printBoard();
    }

    public void notification(String message) {
        printInterrupt(message);
    }
}
