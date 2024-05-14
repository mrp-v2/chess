package model;

import spark.Response;

public record Wrapper(IJsonSerializable data, int statusCode, boolean failure) implements IServiceResponse {

    @Override
    public String send(Response res) {
        res.status(statusCode);
        return data.toJson();
    }

    public static Wrapper success(IJsonSerializable data) {
        return new Wrapper(data, 200, false);
    }
}
