package connection;

import model.AuthResponse;
import model.JsonSerializable;
import model.LoginRequest;
import model.UserData;

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

    // throw a bunch of runtime exceptions because these things shouldn't happen if our client code is correct
    private static <T extends JsonSerializable> ServerResponse<T> request(String method, String path, JsonSerializable requestBody, Class<T> resultType) {
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

        connection.setDoOutput(true);
        connection.addRequestProperty("Content-Type", "application/json");
        try (OutputStream body = connection.getOutputStream()) {
            body.write(JsonSerializable.GSON.toJson(requestBody).getBytes());
        } catch (ConnectException e) {
            return ServerResponse.connectionFailed();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
