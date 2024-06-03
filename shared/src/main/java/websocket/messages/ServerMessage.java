package websocket.messages;

import model.JsonSerializable;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage implements JsonSerializable {
    public final ServerMessageType serverMessageType;

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return serverMessageType == that.serverMessageType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverMessageType);
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public enum ServerMessageType {
        LOAD_GAME, ERROR, NOTIFICATION
    }
}
