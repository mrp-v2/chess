package chess;

import model.JsonSerializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements JsonSerializable {

    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        turn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE(Direction.UP, Direction.UP_LEFT, Direction.UP_RIGHT), BLACK(Direction.DOWN, Direction.DOWN_RIGHT, Direction.DOWN_LEFT);

        public final Direction forward, forwardLeft, forwardRight;

        TeamColor(Direction forward, Direction forwardLeft, Direction forwardRight) {
            this.forward = forward;
            this.forwardLeft = forwardLeft;
            this.forwardRight = forwardRight;
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        } else {
            Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
            Collection<ChessMove> invalids = new ArrayList<>();
            for (ChessMove move : moves) {
                ChessGame clone = makeClone();
                clone.board.makeMove(move);
                if (clone.isInCheck(piece.getTeamColor())) {
                    invalids.add(move);
                }
            }
            for (ChessMove invalid : invalids) {
                moves.remove(invalid);
            }
            return moves;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("No piece at starting position");
        }
        if (turn != board.getPiece(move.getStartPosition()).getTeamColor()) {
            throw new InvalidMoveException("Tried to make a move out of turn");
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Tried to make an illegal move");
        }
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }
        turn = turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for (ChessPosition pos : board.getPiecePositions()) {
            ChessPiece piece = board.getPiece(pos);
            if (piece.getTeamColor() != teamColor) {
                for (ChessMove move : piece.pieceMoves(board, pos)) {
                    ChessPiece endPiece = board.getPiece(move.getEndPosition());
                    if (endPiece != null && endPiece.getTeamColor() == teamColor && endPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return hasNoValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return hasNoValidMoves(teamColor);
    }

    private boolean hasNoValidMoves(TeamColor teamColor) {
        for (ChessPosition pos : board.getPiecePositions()) {
            ChessPiece piece = board.getPiece(pos);
            if (piece.getTeamColor() == teamColor) {
                for (ChessMove move : piece.pieceMoves(board, pos)) {
                    ChessGame clone = makeClone();
                    clone.board.makeMove(move);
                    if (!clone.isInCheck(teamColor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessGame makeClone() {
        ChessGame clone = new ChessGame();
        clone.turn = turn;
        clone.board = board.makeClone();
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
