package model;

import com.google.gson.Gson;

/**
 * Indicates a class can be converted into a JSON string.
 * Provides a default implementation of this behavior suitable for record classes.
 */
public interface JsonSerializable {
    Gson GSON = new Gson();

    default String toJson() {
        return GSON.toJson(this);
    }
}
