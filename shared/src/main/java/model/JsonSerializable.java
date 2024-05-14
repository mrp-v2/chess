package model;

import com.google.gson.Gson;

/**
 * Indicates a class can be converted into a JSON string.
 * Provides a default implementation of this behavior suitable for record classes.
 */
public interface JsonSerializable {
    Gson gson = new Gson();

    default String toJson() {
        return gson.toJson(this);
    }
}
