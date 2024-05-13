package model;

import spark.Response;

import javax.naming.OperationNotSupportedException;

public interface IResponseModel extends IServiceResponse {

    String send(Response res);

    IResponseModel SUCCESS = new IResponseModel() {
        @Override
        public String send(Response res) {
            res.status(statusCode());
            return "";
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
