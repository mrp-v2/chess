package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case PAWN:
                return pawnMoves(board, myPosition);
            case ROOK:
                break;
            case KNIGHT:
                break;
            case BISHOP:
                break;
            case QUEEN:
                break;
            case KING:
                break;
        }
        throw new RuntimeException("Not implemented");
    }

    /**
     * Handles checking for pawn promotion before adding a move.
     *
     * @param start The starting position of the move
     * @param end   The ending position of the move
     * @param moves A collection to add the move(s) to
     */
    private void addPawnMove(ChessPosition start, ChessPosition end, Collection<ChessMove> moves) {
        if (!end.move(color.forward).isValid()) {
            for (PieceType type : PieceType.values()) {
                if (type == PieceType.PAWN) {
                    continue;
                }
                moves.add(new ChessMove(start, end, type));
            }
        }
        moves.add(new ChessMove(start, end));
    }

    /**
     * Handles the possible moves for a pawn.
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition moveOne = myPosition.move(color.forward);
        ChessPiece pieceOne = board.getPiece(moveOne);
        // normal forward moves
        if (pieceOne == null) {
            addPawnMove(myPosition, moveOne, moves);
            if (myPosition.move(color.forward, -1).isValid() && !myPosition.move(color.forward, -2).isValid()) {
                ChessPosition moveTwo = myPosition.move(color.forward, 2);
                ChessPiece pieceTwo = board.getPiece(moveTwo);
                if (pieceTwo == null) {
                    moves.add(new ChessMove(myPosition, moveTwo));
                }
            }
        }
        // diagonal capture moves
        ChessPosition captureLeft = myPosition.move(color.forwardLeft);
        ChessPiece pieceLeft = board.getPiece(captureLeft);
        if (pieceLeft != null && pieceLeft.color != color) {
            addPawnMove(myPosition, captureLeft, moves);
        }
        ChessPosition captureRight = myPosition.move(color.forwardRight);
        ChessPiece pieceRight = board.getPiece(captureRight);
        if (pieceRight != null && pieceRight.color != color) {
            addPawnMove(myPosition, captureRight, moves);
        }
        return moves;
    }
}
