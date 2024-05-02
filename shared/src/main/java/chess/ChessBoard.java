package chess;

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
        board[position.getColumn()][position.getRow()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getColumn()][position.getRow()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[SIZE][SIZE];
        setPiecesRange(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPosition(6, 0), new ChessPosition(6, 7));
        setPiecesRange(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPosition(1, 0), new ChessPosition(1, 7));
        setPieces(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK), new ChessPosition(7, 0), new ChessPosition(7, 7));
        setPieces(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK), new ChessPosition(0, 0), new ChessPosition(0, 7));
        setPieces(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT), new ChessPosition(7, 1), new ChessPosition(7, 6));
        setPieces(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT), new ChessPosition(0, 1), new ChessPosition(0, 6));
        setPieces(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP), new ChessPosition(7, 2), new ChessPosition(7, 5));
        setPieces(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP), new ChessPosition(0, 2), new ChessPosition(0, 5));
        setPieces(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN), new ChessPosition(7, 3));
        setPieces(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN), new ChessPosition(0, 3));
        setPieces(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING), new ChessPosition(7, 4));
        setPieces(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING), new ChessPosition(0, 4));
    }

    private void setPieces(ChessPiece piece, ChessPosition... positions) {
        for (ChessPosition position : positions) {
            board[position.getColumn()][position.getRow()] = piece;
        }
    }

    private void setPiecesRange(ChessPiece piece, ChessPosition start, ChessPosition end) {
        for (int x = start.getColumn(); x <= end.getColumn(); x++) {
            for (int y = start.getRow(); y <= end.getRow(); y++) {
                board[x][y] = piece;
            }
        }
    }
}
