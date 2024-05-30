package connection;

import chess.ChessGame;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class ServerFacade {

    private final int port;

    public ServerFacade() {
        this(8080);
    }

    public ServerFacade(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public ServerResponse<AuthResponse> register(String username, String password, String email) {
        return request("POST", "user", new UserData(username, password, email), AuthResponse.class);
    }

    private <T extends JsonSerializable> ServerResponse<T> request(String method, String path, JsonSerializable requestBody, Class<T> resultType) {
        HttpURLConnection connection = getConnection(method, path);
        ServerResponse<T> result = addRequestData(connection, requestBody);
        if (result != null) {
            return result;
        }
        return processResponse(connection, resultType);
    }

    private HttpURLConnection getConnection(String method, String path) {
        URI uri;
        try {
            uri = new URI(String.format("http://localhost:%d/", port) + path);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) uri.toURL().openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            connection.setRequestMethod(method);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

    private <T extends JsonSerializable> ServerResponse<T> addRequestData(HttpURLConnection connection, JsonSerializable body) {
        if (body == null) {
            return null;
        }
        connection.setDoOutput(true);
        connection.addRequestProperty("Content-Type", "application/json");
        try (OutputStream bodyStream = connection.getOutputStream()) {
            bodyStream.write(JsonSerializable.GSON.toJson(body).getBytes());
        } catch (ConnectException e) {
            return ServerResponse.connectionFailed();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private <T extends JsonSerializable> ServerResponse<T> processResponse(HttpURLConnection connection, Class<T> resultType) {
        try {
            connection.connect();
        } catch (SocketTimeoutException e) {
            return ServerResponse.timedOut();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (resultType == null) {
            try {
                return new ServerResponse<>(null, connection.getResponseCode(), connection.getResponseMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            T result;
            try (InputStream response = connection.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(response);
                result = JsonSerializable.GSON.fromJson(reader, resultType);
            } catch (IOException e) {
                try {
                    if (connection.getResponseCode() != 200) {
                        return new ServerResponse<>(null, connection.getResponseCode(), connection.getResponseMessage());
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
            try {
                return new ServerResponse<>(result, connection.getResponseCode(), connection.getResponseMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ServerResponse<AuthResponse> login(String username, String password) {
        return request("POST", "session", new LoginRequest(username, password), AuthResponse.class);
    }

    public ServerResponse<GameResponse> createGame(String authToken, String name) {
        return authenticatedRequest("POST", "game", authToken, new CreateGameRequest(name), GameResponse.class);
    }

    private <T extends JsonSerializable> ServerResponse<T> authenticatedRequest(String method, String path, String authToken, JsonSerializable requestBody, Class<T> resultType) {
        HttpURLConnection connection = getConnection(method, path);
        connection.addRequestProperty("Authorization", authToken);
        ServerResponse<T> result = addRequestData(connection, requestBody);
        if (result != null) {
            return result;
        }
        return processResponse(connection, resultType);
    }

    public ServerResponse<GamesResponse> getGames(String authToken) {
        return authenticatedRequest("GET", "game", authToken, GamesResponse.class);
    }

    private <T extends JsonSerializable> ServerResponse<T> authenticatedRequest(String method, String path, String authToken, Class<T> resultType) {
        HttpURLConnection connection = getConnection(method, path);
        connection.setDoOutput(true);
        connection.addRequestProperty("Authorization", authToken);
        return processResponse(connection, resultType);
    }

    public ServerResponse<?> joinGame(String authToken, int gameID, ChessGame.TeamColor color) {
        return authenticatedRequest("PUT", "game", authToken, new JoinGameRequest(color, gameID), null);
    }

    public ServerResponse<?> clear() {
        return request("DELETE", "db");
    }

    private ServerResponse<?> request(String method, String path) {
        return request(method, path, null, null);
    }
}
