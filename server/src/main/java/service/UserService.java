package service;

import dataAccess.AuthAccess;
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

    public ServiceResponse register(UserData data) {
        if (data.username() == null || data.password() == null | data.email() == null) {
            return ErrorModel.BAD_REQUEST;
        }
        if (UserAccess.Local.getInstance().createUser(data)) {
            return createAuth(data.username());
        } else {
            return ErrorModel.ALREADY_TAKEN;
        }
    }

    public void clear() {
        UserAccess.Local.getInstance().clear();
    }

    public ServiceResponse createUserAuth(LoginRequest data) {
        if (UserAccess.Local.getInstance().validateUser(data.username(), data.password())) {
            return createAuth(data.username());
        } else {
            return ErrorModel.UNAUTHORIZED;
        }
    }

    private ServiceResponse createAuth(String username) {
        String token = AuthAccess.Local.getInstance().createAuth(username);
        return Wrapper.success(new AuthResponse(token, username));
    }
}
