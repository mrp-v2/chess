package websocket.commands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand {

    public final ChessMove move;

    protected MoveCommand(String authToken, int gameID, ChessMove move) {
        super(authToken, gameID, CommandType.MAKE_MOVE);
        this.move = move;
    }
}
