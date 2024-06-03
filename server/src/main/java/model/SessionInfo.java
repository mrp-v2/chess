package model;

import org.eclipse.jetty.websocket.api.Session;

public record SessionInfo(Session session, String username) {
}
