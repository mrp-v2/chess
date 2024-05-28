package connection;

import model.JsonSerializable;

public record ServerResponse<T extends JsonSerializable>(T data, int responseCode, String responseMessage) {

    public static <T extends JsonSerializable> ServerResponse<T> connectionFailed() {
        return new ServerResponse<>(null, 0, "Error: Failed to connect to server");
    }

    public boolean ok() {
        return responseCode == 200;
    }

    public static <V extends JsonSerializable> ServerResponse<V> timedOut() {
        return new ServerResponse<>(null, 0, "Error: Connection timed out");
    }
}
