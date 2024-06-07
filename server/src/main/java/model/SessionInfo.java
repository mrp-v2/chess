package model;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.util.EnumSet;

public record SessionInfo(Session session, String username, EnumSet<ChessGame.TeamColor> teamColor) {
}
