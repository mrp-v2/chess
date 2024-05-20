package service;

import dataaccess.AuthAccess;
import dataaccess.DataAccessException;
import model.*;

public class AuthService {

    private static AuthService instance;

    private final AuthAccess authAccess;

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    private AuthService() {
        authAccess = AuthAccess.Local.getInstance();
    }

    public void clear() {
        authAccess.clear();
    }

    public ServiceResponse deleteAuth(String token) {
        if (!authAccess.invalidate(token)) {
            return ErrorModel.UNAUTHORIZED;
        } else {
            return ServiceResponse.SUCCESS;
        }
    }

    public ServiceResponse validate(String token) {
        String user;
        try {
            user = authAccess.getUser(token);
        } catch (DataAccessException e) {
            return ErrorModel.UNAUTHORIZED;
        }
        if (user == null) {
            return ErrorModel.UNAUTHORIZED;
        } else {
            return Wrapper.success(new UserResponse(user));
        }
    }
}
