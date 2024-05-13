package model;

import com.google.gson.Gson;

import javax.naming.OperationNotSupportedException;

public interface IServiceResponse {
    Gson gson = new Gson();

    default String toJson() {
        return gson.toJson(this);
    }

    boolean failure();

    int statusCode() throws OperationNotSupportedException;
}
