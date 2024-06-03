package websocket.messages;

import chess.ChessGame;

public class GameMessage extends ServerMessage {

    public final ChessGame game;

    public GameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
