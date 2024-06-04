package websocket.commands;

import model.JsonSerializable;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand implements JsonSerializable {

    public final int gameID;
    public final String authToken;
    public final CommandType commandType;

    protected UserGameCommand(String authToken, int gameID, CommandType commandType) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.commandType = commandType;
    }

    public static UserCommand connect(String authToken, int gameID) {
        return new UserCommand(authToken, gameID, CommandType.CONNECT);
    }

    public static UserCommand leave(String authToken, int gameID) {
        return new UserCommand(authToken, gameID, CommandType.LEAVE);
    }

    public static UserCommand resign(String authToken, int gameID) {
        return new UserCommand(authToken, gameID, CommandType.RESIGN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand that)) {
            return false;
        }
        return commandType == that.commandType && Objects.equals(authToken, that.authToken) && gameID == that.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandType, authToken, gameID);
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    public String getAuthString() {
        return authToken;
    }

    public enum CommandType {
        CONNECT, MAKE_MOVE, LEAVE, RESIGN
    }
}
