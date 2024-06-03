package websocket.messages;

import model.GameData;

public class GameMessage extends ServerMessage {

    public final GameData game;

    public GameMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
