package server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class WebSocketServer {

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println(message);
        session.getRemote().sendString("Response");
    }
}
