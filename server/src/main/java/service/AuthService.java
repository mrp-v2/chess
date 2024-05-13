package service;

import com.google.gson.Gson;
import dataAccess.AuthAccess;
import model.*;
import spark.Request;
import spark.Response;

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

    public IServiceResponse createAuth(String username) {
        String token = AuthAccess.Local.getInstance().createAuth(username);
        return new AuthData(token, username);
    }

    public IServiceResponse deleteAuth(String token) {
        if (!AuthAccess.Local.getInstance().invalidate(token)) {
            return ErrorModel.UNAUTHORIZED;
        } else {
            return IResponseModel.SUCCESS;
        }
    }

    public IServiceResponse validate(String token) {
        String user = AuthAccess.Local.getInstance().getUser(token);
        if (user == null) {
            return ErrorModel.UNAUTHORIZED;
        } else {
            return new UserResponse(user);
        }
    }
}
