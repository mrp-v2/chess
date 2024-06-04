package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import websocket.commands.MoveCommand;
import websocket.commands.UserCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.GameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

/**
 * Indicates a class can be converted into a JSON string.
 * Provides a default implementation of this behavior suitable for record classes.
 */
public interface JsonSerializable {
    Gson GSON = new GsonBuilder().registerTypeAdapter(UserGameCommand.class, (JsonDeserializer<UserGameCommand>) (element, type, context) -> {
        if (element.isJsonObject()) {
            String commandType = element.getAsJsonObject().get("commandType").getAsString();
            return switch (UserGameCommand.CommandType.valueOf(commandType)) {
                case MAKE_MOVE -> context.deserialize(element, MoveCommand.class);
                case CONNECT, LEAVE, RESIGN -> context.deserialize(element, UserCommand.class);
            };
        }
        return null;
    }).registerTypeAdapter(ServerMessage.class, (JsonDeserializer<ServerMessage>) (element, type, context) -> {
        if (element.isJsonObject()) {
            String messageType = element.getAsJsonObject().get("serverMessageType").getAsString();
            return switch (ServerMessage.ServerMessageType.valueOf(messageType)) {
                case LOAD_GAME -> context.deserialize(element, GameMessage.class);
                case NOTIFICATION, ERROR -> context.deserialize(element, NotificationMessage.class);
            };
        }
        return null;
    }).create();

    default String toJson() {
        return GSON.toJson(this);
    }
}
