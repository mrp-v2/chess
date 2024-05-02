package chess;

public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1),
    UP_RIGHT(-1, 1),
    UP_LEFT(-1, -1),
    DOWN_RIGHT(1, 1),
    DOWN_LEFT(1, -1);

    public final int rowChange, columnChange;

    Direction(int rowChange, int columnChange) {
        this.rowChange = rowChange;
        this.columnChange = columnChange;
    }

    /**
     * Returns a new ChessPosition the has been moved in this direction.
     *
     * @param pos    The starting ChessPosition
     * @param spaces How far to move
     */
    public ChessPosition apply(ChessPosition pos, int spaces) {
        return new ChessPosition(pos.getRow() + rowChange * spaces, pos.getColumn() + columnChange * spaces);
    }
}
