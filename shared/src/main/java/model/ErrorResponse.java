package model;

public record ErrorResponse(String message) implements IServiceResponse {
    @Override
    public boolean failure() {
        return true;
    }

    @Override
    public int statusCode() {
        return 400;
    }
}
