package websocket.commands;

public class UserCommand extends UserGameCommand {
    protected UserCommand(String authToken, int gameID, CommandType commandType) {
        super(authToken, gameID, commandType);
    }
}
