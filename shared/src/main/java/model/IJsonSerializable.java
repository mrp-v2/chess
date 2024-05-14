package model;

import com.google.gson.Gson;

public interface IJsonSerializable {
    Gson gson = new Gson();

    default String toJson() {
        return gson.toJson(this);
    }
}
