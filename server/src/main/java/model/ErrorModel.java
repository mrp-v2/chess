package model;

import spark.Response;

public record ErrorModel(ErrorResponse data, int statusCode) implements IServiceResponse {

    private ErrorModel(String message, int statusCode) {
        this(new ErrorResponse(message), statusCode);
    }

    @Override
    public String toJson() {
        return data.toJson();
    }

    public String send(Response res) {
        res.status(statusCode);
        return toJson();
    }

    @Override
    public boolean failure() {
        return true;
    }

    public static ErrorModel UNAUTHORIZED = new ErrorModel("Error: unauthorized", 401);
    public static ErrorModel BAD_REQUEST = new ErrorModel("Error: bad request", 400);
    public static ErrorModel ALREADY_TAKEN = new ErrorModel("Error: already taken", 403);
}
