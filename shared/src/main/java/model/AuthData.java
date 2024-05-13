package model;

public record AuthData(String authToken, String username) implements IServiceResponse {
    @Override
    public boolean failure() {
        return false;
    }

    @Override
    public int statusCode() {
        return 200;
    }
}
