package model;

public record UserResponse(String username) implements IServiceResponse {
    @Override
    public boolean failure() {
        return false;
    }

    @Override
    public int statusCode() {
        return 200;
    }
}
