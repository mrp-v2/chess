package service;

import dataAccess.UserAccess;
import model.*;
import spark.Request;
import spark.Response;

public class UserService {

    private static UserService instance;

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public IServiceResponse register(UserData data) {
        if (data.username() == null || data.password() == null | data.email() == null) {
            return ErrorModel.BAD_REQUEST;
        }
        if (UserAccess.Local.getInstance().createUser(data)) {
            return IResponseModel.SUCCESS;
        } else {
            return ErrorModel.ALREADY_TAKEN;
        }
    }

    public void clear() {
        UserAccess.Local.getInstance().clear();
    }

    public IServiceResponse getUser(LoginRequest data) {
        UserData result = UserAccess.Local.getInstance().getUser(data.username(), data.password());
        if (result != null) {
            return IResponseModel.SUCCESS;
        } else {
            return ErrorModel.UNAUTHORIZED;
        }
    }
}
