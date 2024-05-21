package service;

import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

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
        userAccess = SQLUserAccess.getInstance();
        authAccess = SQLAuthAccess.getInstance();
    }

    public ServiceResponse register(UserData data) {
        if (data.username() == null || data.password() == null | data.email() == null) {
            return ErrorModel.BAD_REQUEST;
        }
        try {
            if (userAccess.createUser(data.username(), BCrypt.hashpw(data.password(), BCrypt.gensalt()), data.email())) {
                return createAuth(data.username());
            } else {
                return ErrorModel.ALREADY_TAKEN;
            }
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
    }

    public ServiceResponse clear() {
        try {
            userAccess.clear();
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
        return ServiceResponse.SUCCESS;
    }

    public ServiceResponse createUserAuth(LoginRequest data) {
        try {
            String hash = userAccess.getUserPasswordHash(data.username());
            if (hash == null) {
                return ErrorModel.UNAUTHORIZED;
            }
            if (BCrypt.checkpw(data.password(), hash)) {
                return createAuth(data.username());
            } else {
                return ErrorModel.UNAUTHORIZED;
            }
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
    }

    private ServiceResponse createAuth(String username) {
        String token;
        try {
            token = authAccess.createAuth(username);
        } catch (DataAccessException e) {
            return ErrorModel.DATABASE_ERROR;
        }
        return Wrapper.success(new AuthResponse(token, username));
    }
}
