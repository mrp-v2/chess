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
        Collection<ChessMove> moves = new ArrayList<>();
        switch (type) {
            case PAWN:
                addPawnMoves(board, myPosition, moves);
                break;
            case ROOK:
                addRookMoves(board, myPosition, moves);
                break;
            case KNIGHT:
                addKnightMoves(board, myPosition, moves);
                break;
            case BISHOP:
                addBishopMoves(board, myPosition, moves);
                break;
            case QUEEN:
                addRookMoves(board, myPosition, moves);
                addBishopMoves(board, myPosition, moves);
                break;
            case KING:
                for (Direction dir : Direction.values()) {
                    ChessPosition pos = myPosition.move(dir);
                    if (pos.isValid()) {
                        ChessPiece piece = board.getPiece(pos);
                        if (piece == null || piece.color != color) {
                            moves.add(new ChessMove(myPosition, pos));
                        }
                    }
                }
                break;
        }
        return moves;
    }

    private void addKnightMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        for (Direction dir : Direction.CARDINAL) {
            ChessPosition left = start.move(dir, 2).move(dir.left());
            if (left.isValid()) {
                ChessPiece piece = board.getPiece(left);
                if (piece == null || piece.color != color) {
                    moves.add(new ChessMove(start, left));
                }
            }
            ChessPosition right = start.move(dir, 2).move(dir.right());
            if (right.isValid()) {
                ChessPiece piece = board.getPiece(right);
                if (piece == null || piece.color != color) {
                    moves.add(new ChessMove(start, right));
                }
            }
        }
    }

    private void addBishopMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        addMoves(board, start, Direction.UP_RIGHT, moves);
        addMoves(board, start, Direction.UP_LEFT, moves);
        addMoves(board, start, Direction.DOWN_RIGHT, moves);
        addMoves(board, start, Direction.DOWN_LEFT, moves);
    }

    private void addRookMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        addMoves(board, start, Direction.UP, moves);
        addMoves(board, start, Direction.DOWN, moves);
        addMoves(board, start, Direction.LEFT, moves);
        addMoves(board, start, Direction.RIGHT, moves);
    }

    private void addMoves(ChessBoard board, ChessPosition start, Direction dir, Collection<ChessMove> moves) {
        ChessPosition pos = start.move(dir);
        while (pos.isValid()) {
            ChessPiece piece = board.getPiece(pos);
            if (piece == null) {
                moves.add(new ChessMove(start, pos));
            } else {
                if (piece.color != color) {
                    moves.add(new ChessMove(start, pos));
                }
                break;
            }
            pos = pos.move(dir);
        }
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
    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        // normal forward moves
        ChessPosition moveOne = myPosition.move(color.forward);
        if (moveOne.isValid()) {
            ChessPiece pieceOne = board.getPiece(moveOne);
            if (pieceOne == null) {
                addPawnMove(myPosition, moveOne, moves);
                if (myPosition.move(color.forward, -1).isValid() && !myPosition.move(color.forward, -2).isValid()) {
                    ChessPosition moveTwo = myPosition.move(color.forward, 2);
                    if (moveTwo.isValid()) {
                        ChessPiece pieceTwo = board.getPiece(moveTwo);
                        if (pieceTwo == null) {
                            moves.add(new ChessMove(myPosition, moveTwo));
                        }
                    }
                }
            }
        }
        // diagonal capture moves
        ChessPosition captureLeft = myPosition.move(color.forwardLeft);
        if (captureLeft.isValid()) {
            ChessPiece pieceLeft = board.getPiece(captureLeft);
            if (pieceLeft != null && pieceLeft.color != color) {
                addPawnMove(myPosition, captureLeft, moves);
            }
        }
        ChessPosition captureRight = myPosition.move(color.forwardRight);
        if (captureRight.isValid()) {
            ChessPiece pieceRight = board.getPiece(captureRight);
            if (pieceRight != null && pieceRight.color != color) {
                addPawnMove(myPosition, captureRight, moves);
            }
        }
    }
}
