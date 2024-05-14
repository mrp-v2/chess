package model;

public record AuthResponse(String authToken, String username) implements JsonSerializable {
}
