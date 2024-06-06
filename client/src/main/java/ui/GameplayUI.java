package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import connection.ServerFacade;
import connection.WebSocketFacade;
import model.AuthResponse;
import model.GameData;

import java.util.Collection;
import java.util.Scanner;

public class GameplayUI extends PostLoginUI {

    private static final String HELP = """
                                       redraw
                                       leave
                                       move <position> <position>
                                       resign
                                       moves <position>
                                       help""";

    private static final String OBSERVER_HELP = """
                                                redraw
                                                leave
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
                socketFacade.leave(auth.authToken(), game.gameID());
                return false;
            case "move":
                if (playerColor == null) {
                    printHelp();
                    break;
                }
                if (args.length == 3) {
                    handleMove(args[1], args[2], null);
                    break;
                }
                if (args.length == 4) {
                    handleMove(args[1], args[2], args[3]);
                }
                printHelp();
                break;
            case "resign":
                if (playerColor == null) {
                    printHelp();
                    break;
                }
                handleResign();
                break;
            case "moves":
                handleShowMoves(args[1]);
                break;
            case "help":
                printHelp();
                break;
        }
        return true;
    }

    private void printBoard() {
        PrintBoardHelper.printBoard(game.game().getBoard(), playerColor);
    }

    @Override
    protected void printHelp() {
        if (playerColor == null) {
            System.out.println(OBSERVER_HELP);
        } else {
            System.out.println(HELP);
        }
    }

    private void handleMove(String from, String to, String promotionPiece) {
        if (game.game().getTeamTurn() == null) {
            System.out.println("game is over");
        }
        if (playerColor != game.game().getTeamTurn()) {
            System.out.println("not your turn");
            return;
        }
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

        ChessPiece.PieceType promotion = null;
        if (promotionPiece != null) {
            try {
                promotion = ChessPiece.PieceType.valueOf(promotionPiece);
            } catch (IllegalArgumentException e) {
                System.out.printf("'%s' is not a valid piece type\n", promotionPiece);
            }
        }
        socketFacade.move(auth.authToken(), game.gameID(), new ChessMove(fromPos, toPos, promotion));
    }

    private void handleResign() {
        ResignUI ui = new ResignUI(scanner, serverFacade);
        ui.run();
    }

    private void handleShowMoves(String position) {
        if (position.length() != 2) {
            System.out.printf("invalid position '%s', should be two characters\n", position);
            return;
        }
        ChessPosition pos = new ChessPosition(position);
        if (!pos.isValid()) {
            System.out.printf("invalid position '%s'\n", pos);
            return;
        }
        Collection<ChessMove> moves = game.game().validMoves(pos);
        if (moves == null) {
            System.out.printf("no valid moves for piece at %s\n", position);
            return;
        }
        PrintBoardHelper.printBoard(game.game().getBoard(), playerColor, moves);
    }

    protected void doResign() {
        socketFacade.resign(auth.authToken(), game.gameID());
    }

    public void updateBoard(GameData game) {
        this.game = game;
        printBoardInterrupt();
    }

    public void printBoardInterrupt() {
        printInterrupt(this::printBoard);
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
