package service;

import dataAccess.AuthAccess;
import dataAccess.DataAccessException;
import model.*;

public class AuthService {

    private static AuthService instance;

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public void clear() {
        AuthAccess.Local.getInstance().clear();
    }

    public IServiceResponse deleteAuth(String token) {
        if (!AuthAccess.Local.getInstance().invalidate(token)) {
            return ErrorModel.UNAUTHORIZED;
        } else {
            return IServiceResponse.SUCCESS;
        }
    }

    public IServiceResponse validate(String token) {
        String user;
        try {
            user = AuthAccess.Local.getInstance().getUser(token);
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
