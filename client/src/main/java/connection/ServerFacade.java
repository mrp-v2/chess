package connection;

import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class ServerFacade {

    public static ServerResponse<AuthResponse> register(String username, String password, String email) {
        return request("POST", "user", new UserData(username, password, email), AuthResponse.class);
    }

    public static ServerResponse<AuthResponse> login(String username, String password) {
        return request("POST", "session", new LoginRequest(username, password), AuthResponse.class);
    }

    public static ServerResponse<GameResponse> createGame(String username, String authToken, String name) {
        return authenticatedRequest("POST", "game", username, authToken, new CreateGameRequest(name), GameResponse.class);
    }

    private static HttpURLConnection getConnection(String method, String path) {
        URI uri;
        try {
            uri = new URI("http://localhost:8080/" + path);
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

    private static <T extends JsonSerializable> ServerResponse<T> addRequestData(HttpURLConnection connection, JsonSerializable body) {
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

    private static <T extends JsonSerializable> ServerResponse<T> authenticatedRequest(String method, String path, String username, String authToken, JsonSerializable requestBody, Class<T> resultType) {
        HttpURLConnection connection = getConnection(method, path);
        ServerResponse<T> result = addRequestData(connection, requestBody);
        if (result != null) {
            return result;
        }
        connection.addRequestProperty("Authorization", String.format("%s:%s", username, authToken));
        return processResponse(connection, resultType);
    }

    private static <T extends JsonSerializable> ServerResponse<T> request(String method, String path, JsonSerializable requestBody, Class<T> resultType) {
        HttpURLConnection connection = getConnection(method, path);
        ServerResponse<T> result = addRequestData(connection, requestBody);
        if (result != null) {
            return result;
        }
        return processResponse(connection, resultType);
    }

    private static <T extends JsonSerializable> ServerResponse<T> processResponse(HttpURLConnection connection, Class<T> resultType) {
        try {
            connection.connect();
        } catch (SocketTimeoutException e) {
            return ServerResponse.timedOut();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        T result;
        try (InputStream response = connection.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(response);
            result = JsonSerializable.GSON.fromJson(reader, resultType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return new ServerResponse<>(result, connection.getResponseCode(), connection.getResponseMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
