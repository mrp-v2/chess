package service;

import dataaccess.AuthAccess;
import dataaccess.UserAccess;
import model.*;

public class UserService {

    private static UserService instance;

    private final UserAccess userAccess;
    private final AuthAccess authAccess;

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private UserService() {
        userAccess = UserAccess.Local.getInstance();
        authAccess = AuthAccess.Local.getInstance();
    }

    public ServiceResponse register(UserData data) {
        if (data.username() == null || data.password() == null | data.email() == null) {
            return ErrorModel.BAD_REQUEST;
        }
        if (userAccess.createUser(data)) {
            return createAuth(data.username());
        } else {
            return ErrorModel.ALREADY_TAKEN;
        }
    }

    public void clear() {
        UserAccess.Local.getInstance().clear();
    }

    public ServiceResponse createUserAuth(LoginRequest data) {
        if (userAccess.validateUser(data.username(), data.password())) {
            return createAuth(data.username());
        } else {
            return ErrorModel.UNAUTHORIZED;
        }
    }

    private ServiceResponse createAuth(String username) {
        String token = authAccess.createAuth(username);
        return Wrapper.success(new AuthResponse(token, username));
    }
}
