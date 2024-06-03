package ui;

import chess.ChessGame;
import connection.ServerFacade;
import connection.WebSocketFacade;

import java.util.Scanner;

public class GameplayUI extends UserInputHandler {


    private final ChessGame game;
    private final WebSocketFacade socketFacade;
    private final ChessGame.TeamColor playerColor;

    public GameplayUI(Scanner scanner, ChessGame game, ChessGame.TeamColor playerColor, ServerFacade serverFacade) {
        super(scanner, "exit", serverFacade);
        this.game = game;
        this.playerColor = playerColor;
        this.socketFacade = new WebSocketFacade(serverFacade.getPort());
    }

    @Override
    protected void printHelp() {
        System.out.println("Gameplay help - coming soon");
    }

    @Override
    protected void handleArgs(String[] args) {
        switch (args[0]) {
            case "redraw":
                PrintBoardHelper.printBoard(game.getBoard(), playerColor);
                break;
            case "leave":
                // exit game - color loses its user
                break;
            case "move":
                // make a move
                break;
            case "resign":
                // ask for confirmation before resigning, doesn't actually leave the game after resigning
                break;
            case "moves":
                // show the legal moves for a piece
                break;
        }
    }
}
