package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition start, end;
    private final ChessPiece.PieceType promoted;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        start = startPosition;
        end = endPosition;
        promoted = promotionPiece;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this(startPosition, endPosition, null);
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promoted;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ChessMove move) {
            return start.equals(move.start) && end.equals(move.end) && promoted == move.promoted;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promoted);
    }

    @Override
    public String toString() {
        return "ChessMove{start=" + start + ", end=" + end + ", promoted=" + promoted + '}';
    }
}
