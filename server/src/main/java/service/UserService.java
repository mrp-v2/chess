package service;

import dataAccess.UserAccess;
import model.*;

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
            return IServiceResponse.SUCCESS;
        } else {
            return ErrorModel.ALREADY_TAKEN;
        }
    }

    public void clear() {
        UserAccess.Local.getInstance().clear();
    }

    public IServiceResponse getUser(LoginRequest data) {
        if (UserAccess.Local.getInstance().validateUser(data.username(), data.password())) {
            return IServiceResponse.SUCCESS;
        } else {
            return ErrorModel.UNAUTHORIZED;
        }
    }
}
