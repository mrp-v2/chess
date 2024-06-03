package model;

import spark.Response;

/**
 * Used to implement {@link ServiceResponse} for all erroneous responses.
 * Provides static instances of common errors.
 */
public record ErrorModel(ErrorResponse data, int statusCode) implements ServiceResponse {

    public static final ErrorModel BAD_REQUEST = new ErrorModel("Error: bad request", 400);
    public static final ErrorModel UNAUTHORIZED = new ErrorModel("Error: unauthorized", 401);
    public static final ErrorModel ALREADY_TAKEN = new ErrorModel("Error: already taken", 403);
    public static final ErrorModel DATABASE_ERROR = new ErrorModel("Error: error while interacting with database", 500);
    public static final ErrorModel ALREADY_JOINED = new ErrorModel("Error: already joined this game", 403);

    private ErrorModel(String message, int statusCode) {
        this(new ErrorResponse(message), statusCode);
    }

    public String send(Response res) {
        res.status(statusCode);
        return toJson();
    }

    @Override
    public String toJson() {
        return data.toJson();
    }

    @Override
    public boolean failure() {
        return true;
    }
}
