package ui;

import chess.ChessGame;
import connection.ServerFacade;
import connection.WebSocketFacade;
import model.AuthResponse;
import model.GameData;

import java.util.Scanner;

public class GameplayUI extends UserInputHandler {

    private final WebSocketFacade socketFacade;
    private final ChessGame.TeamColor playerColor;
    private final AuthResponse auth;
    private GameData game;

    public GameplayUI(Scanner scanner, GameData game, ChessGame.TeamColor playerColor, AuthResponse auth, ServerFacade serverFacade) {
        super(scanner, "exit", serverFacade);
        this.game = game;
        this.playerColor = playerColor;
        this.auth = auth;
        this.socketFacade = new WebSocketFacade(serverFacade.getPort(), this);
    }

    @Override
    protected void printHelp() {
        System.out.println("Gameplay help - coming soon");
    }

    @Override
    protected void handleArgs(String[] args) {
        switch (args[0]) {
            case "redraw":
                printBoard();
                break;
            case "leave":
                // exit game - color loses its user
                socketFacade.leave(auth.authToken(), game.gameID());
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

    public void printBoard() {
        PrintBoardHelper.printBoard(game.game().getBoard(), playerColor);
    }

    public void updateBoard(GameData game) {
        this.game = game;
        printBoard();
    }
}
