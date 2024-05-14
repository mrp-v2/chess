package model;

import spark.Response;

public record ErrorModel(String message, int statusCode) implements IServiceResponse {
    @Override
    public String toJson() {
        return new ErrorResponse(message).toJson();
    }

    public String send(Response res) {
        res.status(statusCode);
        return toJson();
    }

    @Override
    public IJsonSerializable data() {
        return null;
    }

    @Override
    public boolean failure() {
        return true;
    }

    public static ErrorModel UNAUTHORIZED = new ErrorModel("Error: unauthorized", 401);
    public static ErrorModel BAD_REQUEST = new ErrorModel("Error: bad request", 400);
    public static ErrorModel ALREADY_TAKEN = new ErrorModel("Error: already taken", 403);
}
