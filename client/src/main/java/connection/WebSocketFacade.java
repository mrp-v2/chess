package connection;

import model.JsonSerializable;
import ui.GameplayUI;
import websocket.commands.UserGameCommand;
import websocket.messages.GameMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade implements MessageHandler.Whole<String> {

    private final Session session;
    private final GameplayUI ui;

    public WebSocketFacade(int port, GameplayUI ui, String auth, int gameID) {
        this.ui = ui;
        try {
            URI uri = new URI("ws://localhost:" + port + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    if (!session.equals(WebSocketFacade.this.session)) {
                        throw new RuntimeException("Multiple sessions");
                    }
                    sendData(UserGameCommand.connect(auth, gameID));
                }
            }, uri);
            this.session.addMessageHandler(this);
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendData(JsonSerializable data) {
        try {
            session.getBasicRemote().sendText(data.toJson());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onMessage(String message) {
        ServerMessage serverMessage = JsonSerializable.GSON.fromJson(message, ServerMessage.class);
        switch (serverMessage.serverMessageType) {
            case LOAD_GAME:
                ui.updateBoard(((GameMessage) serverMessage).game);
        }
    }

    public void leave(String auth, int gameID) {
        sendData(UserGameCommand.leave(auth, gameID));
    }
}
