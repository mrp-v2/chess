package model;

import spark.Response;

/**
 * Used to implement {@link ServiceResponse} for all erroneous responses.
 * Provides static instances of common errors.
 */
public record ErrorModel(ErrorResponse data, int statusCode) implements ServiceResponse {

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

    public static final ErrorModel BAD_REQUEST = new ErrorModel("Error: bad request", 400);
    public static final ErrorModel UNAUTHORIZED = new ErrorModel("Error: unauthorized", 401);
    public static final ErrorModel ALREADY_TAKEN = new ErrorModel("Error: already taken", 403);
}
