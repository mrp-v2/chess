package model;

import spark.Response;

/**
 * Provides an easy conversion from a {@link JsonSerializable} to a {@link ServiceResponse}.
 */
public record Wrapper(JsonSerializable data, int statusCode, boolean failure) implements ServiceResponse {

    @Override
    public String send(Response res) {
        res.status(statusCode);
        return data.toJson();
    }

    /**
     * Wraps a {@link JsonSerializable} into a {@link ServiceResponse} indicating success.
     */
    public static Wrapper success(JsonSerializable data) {
        return new Wrapper(data, 200, false);
    }
}
