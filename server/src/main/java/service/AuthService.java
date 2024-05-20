package service;

import dataaccess.AuthAccess;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthAccess;
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
        authAccess = SQLAuthAccess.getInstance();
    }

    public ServiceResponse clear() {
        try {
            authAccess.clear();
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
        return ServiceResponse.SUCCESS;
    }

    public ServiceResponse deleteAuth(String token) {
        try {
            if (!authAccess.invalidate(token)) {
                return ErrorModel.UNAUTHORIZED;
            } else {
                return ServiceResponse.SUCCESS;
            }
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
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
