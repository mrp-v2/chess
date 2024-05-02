package chess;

public enum Direction {
    UP(1, 0),
    DOWN(-1, 0),
    LEFT(0, -1, DOWN, UP),
    RIGHT(0, 1, UP, DOWN),
    UP_RIGHT(1, 1),
    UP_LEFT(1, -1),
    DOWN_RIGHT(-1, 1),
    DOWN_LEFT(-1, -1);

    public static final Direction[] CARDINAL = new Direction[]{UP, LEFT, DOWN, RIGHT};

    public final int rowChange, columnChange;

    private Direction left, right;

    Direction(int rowChange, int columnChange) {
        this.rowChange = rowChange;
        this.columnChange = columnChange;
    }

    Direction(int rowChange, int columnChange, Direction left) {
        this(rowChange, columnChange);
        this.left = left;
        left.right = this;
    }

    Direction(int rowChange, int columnChange, Direction left, Direction right) {
        this(rowChange, columnChange, left);
        this.right = right;
        right.left = this;
    }

    public Direction left() {
        return left;
    }

    public Direction right() {
        return right;
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
