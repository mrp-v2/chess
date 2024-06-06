package ui;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PrintBoardHelper {
    private static final String[] VERTICAL_LABELS = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};

    public static void printBoard(ChessBoard board, ChessGame.TeamColor perspective) {
        printBoard(board, perspective, Collections.emptyList());
    }

    public static void printWhite(ChessBoard board, ChessPosition origin, Collection<ChessPosition> destinations) {
        for (int i = 7; i >= 0; i--) {
            System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW + " " + EscapeSequences.SET_TEXT_COLOR_BLACK + VERTICAL_LABELS[i] + EscapeSequences.RESET_TEXT_COLOR + " ");
            for (int col = 1; col <= 8; col++) {
                ChessPosition current = new ChessPosition(i + 1, col);
                printPosition(board, current, origin, destinations);
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }
        System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW + EscapeSequences.SET_TEXT_COLOR_BLACK + "   ");
        for (int col = 1; col <= 8; col++) {
            printColumnLabel(col);
        }
        System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
    }

    private static void printColumnLabel(int col) {
        System.out.print(" " + col + EscapeSequences.SHORT_EMPTY);
    }

    private static void printPosition(ChessBoard board, ChessPosition position, ChessPosition origin, Collection<ChessPosition> destinations) {
        if (position.equals(origin)) {
            printBoardBody(board, position.getRow(), position.getColumn(), EscapeSequences.SET_BG_COLOR_BLUE);
        } else if (destinations.contains(position)) {
            printBoardBody(board, position.getRow(), position.getColumn(), EscapeSequences.SET_BG_COLOR_GREEN);
        } else {
            printBoardBody(board, position.getRow(), position.getColumn(), null);
        }
    }

    private static void printBoardBody(ChessBoard board, int row, int col, String colorOverride) {
        if (colorOverride == null) {
            colorOverride = col % 2 == row % 2 ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        }
        System.out.print(colorOverride);
        System.out.print(translate(board, new ChessPosition(row, col)));
    }

    private static String translate(ChessBoard board, ChessPosition pos) {
        ChessPiece piece = board.getPiece(pos);
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }
        return switch (piece.getPieceType()) {
            case PAWN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            case ROOK ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case KNIGHT ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case BISHOP ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case QUEEN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case KING ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
        };
    }

    public static void printBlack(ChessBoard board, ChessPosition origin, Collection<ChessPosition> destinations) {
        for (int i = 0; i < 8; i++) {
            System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW + " " + EscapeSequences.SET_TEXT_COLOR_BLACK + VERTICAL_LABELS[i] + EscapeSequences.RESET_TEXT_COLOR + " ");
            for (int col = 8; col > 0; col--) {
                ChessPosition current = new ChessPosition(i + 1, col);
                printPosition(board, current, origin, destinations);
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }
        System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW + EscapeSequences.SET_TEXT_COLOR_BLACK + "   ");
        for (int col = 8; col > 0; col--) {
            printColumnLabel(col);
        }
        System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
    }

    public static void printBoard(ChessBoard board, ChessGame.TeamColor perspective, Collection<ChessMove> moves) {
        ChessPosition origin = null;
        Collection<ChessPosition> destinations = new ArrayList<>();
        if (!moves.isEmpty()) {
            origin = moves.iterator().next().getStartPosition();
            for (ChessMove move : moves) {
                destinations.add(move.getEndPosition());
            }
        }
        if (perspective == ChessGame.TeamColor.BLACK) {
            printBlack(board, origin, destinations);
        } else {
            printWhite(board, origin, destinations);
        }
    }
}
