package model;

import spark.Response;

public interface IServiceResponse extends IJsonSerializable {

    boolean failure();

    int statusCode();

    String send(Response res);

    IJsonSerializable data();

    IServiceResponse SUCCESS = new IServiceResponse() {
        @Override
        public String send(Response res) {
            res.status(statusCode());
            return "";
        }

        @Override
        public IJsonSerializable data() {
            return null;
        }

        @Override
        public boolean failure() {
            return false;
        }

        @Override
        public int statusCode() {
            return 200;
        }
    };
}
