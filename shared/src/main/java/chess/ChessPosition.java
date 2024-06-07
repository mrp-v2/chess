package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row, col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public ChessPosition(String from) {
        if (from.length() != 2) {
            throw new IllegalArgumentException("from string should be exactly two characters");
        }
        col = from.charAt(0) - 'a' + 1;
        row = from.charAt(1) - '1' + 1;
    }

    @Override
    public String toString() {
        return new StringBuilder().append((char) ('a' - 1 + col)).append((char) ('1' - 1 + row)).toString();
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof ChessPosition pos) {
            return row == pos.row && col == pos.col;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /**
     * Checks if this position is within the board.
     */
    public boolean isValid() {
        return row > 0 && row <= ChessBoard.SIZE && col > 0 && col <= ChessBoard.SIZE;
    }

    /**
     * Returns a new position the specified direction and distance from this position.
     *
     * @param dir    The direction to move in
     * @param spaces How far to move
     * @return A new ChessPosition
     */
    public ChessPosition move(Direction dir, int spaces) {
        return dir.apply(this, spaces);
    }

    public ChessPosition move(Direction dir) {
        return dir.apply(this, 1);
    }
}
