package chess;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public static int SIZE = 8;

    /**
     * Stores pieces by column, then row. e.g. board[x][y] or board[column][row]
     */
    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[SIZE][SIZE];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getColumn() - 1][position.getRow() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getColumn() - 1][position.getRow() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[SIZE][SIZE];
        setPiecesRange(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPosition(7, 1), new ChessPosition(7, 8));
        setPiecesRange(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPosition(2, 1), new ChessPosition(2, 8));
        setPieces(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK), new ChessPosition(8, 1), new ChessPosition(8, 8));
        setPieces(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK), new ChessPosition(1, 1), new ChessPosition(1, 8));
        setPieces(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT), new ChessPosition(8, 2), new ChessPosition(8, 7));
        setPieces(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT), new ChessPosition(1, 2), new ChessPosition(1, 7));
        setPieces(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP), new ChessPosition(8, 3), new ChessPosition(8, 6));
        setPieces(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP), new ChessPosition(1, 3), new ChessPosition(1, 6));
        setPieces(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN), new ChessPosition(8, 4));
        setPieces(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN), new ChessPosition(1, 4));
        setPieces(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING), new ChessPosition(8, 5));
        setPieces(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING), new ChessPosition(1, 5));
    }

    private void setPieces(ChessPiece piece, ChessPosition... positions) {
        for (ChessPosition position : positions) {
            addPiece(position, piece);
        }
    }

    private void setPiecesRange(ChessPiece piece, ChessPosition start, ChessPosition end) {
        for (int col = start.getColumn(); col <= end.getColumn(); col++) {
            for (int row = start.getRow(); row <= end.getRow(); row++) {
                addPiece(new ChessPosition(row, col), piece);
            }
        }
    }

    public Iterable<ChessPosition> getPiecePositions() {
        class itr implements Iterator<ChessPosition>, Iterable<ChessPosition> {

            private int row, column;

            itr() {
                row = 1;
                column = 1;
                advance();
            }

            @Override
            public boolean hasNext() {
                return row <= SIZE && column <= SIZE;
            }

            @Override
            public ChessPosition next() {
                ChessPosition next = new ChessPosition(row, column);
                advance();
                return next;
            }

            private void advance() {
                column++;
                if (column > SIZE) {
                    column = 1;
                    row++;
                    if (row > SIZE) {
                        return;
                    }
                }
                while (board[column - 1][row - 1] == null) {
                    column++;
                    if (column > SIZE) {
                        column = 1;
                        row++;
                        if (row > SIZE) {
                            return;
                        }
                    }
                }
            }

            @Override
            public Iterator<ChessPosition> iterator() {
                return this;
            }
        }

        return new itr();
    }

    public ChessBoard makeClone() {
        ChessBoard clone = new ChessBoard();
        for (ChessPosition pos : getPiecePositions()) {
            clone.addPiece(pos, getPiece(pos));
        }
        return clone;
    }

    public void makeMove(ChessMove move) {
        ChessPiece piece = getPiece(move.getStartPosition());
        addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        } else {
            addPiece(move.getEndPosition(), piece);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
