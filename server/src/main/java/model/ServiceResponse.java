package model;

import spark.Response;

/**
 * Specifies methods for getting information related to the server HTTP response.
 * Also specifies a method for conveniently sending the reponse.
 */
public interface ServiceResponse extends JsonSerializable {

    boolean failure();

    int statusCode();

    String send(Response res);

    JsonSerializable data();

    /**
     * Used when an operation is successful, but has no data associated with it.
     */
    ServiceResponse SUCCESS = new ServiceResponse() {
        @Override
        public String send(Response res) {
            res.status(statusCode());
            return "";
        }

        @Override
        public JsonSerializable data() {
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
