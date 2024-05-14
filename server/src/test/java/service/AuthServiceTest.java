package service;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private static final UserData user1 = new UserData("user1", "password1", "email1");
    private static final LoginRequest login1 = new LoginRequest(user1.username(), user1.password());
    private String token1, token2;
    private static final String INVALID_AUTH = "INVALID_AUTH";

    @Test
    void getInstance() {
        assertNotNull(AuthService.getInstance());
    }

    @AfterEach
    void cleanup() {
        AuthService.getInstance().clear();
        UserService.getInstance().clear();
    }

    @BeforeEach
    void setup() {
        IServiceResponse res = UserService.getInstance().register(user1);
        AuthData data = (AuthData) res.data();
        token1 = data.authToken();
        res = UserService.getInstance().createUserAuth(login1);
        token2 = ((AuthData) res.data()).authToken();
    }

    @Test
    void clear() {
        AuthService.getInstance().clear();
        assertEquals(401, AuthService.getInstance().validate(token1).statusCode());
    }

    @Test
    void deleteAuth() {
        assertEquals(200, AuthService.getInstance().deleteAuth(token1).statusCode());
        assertEquals(401, AuthService.getInstance().validate(token1).statusCode());
    }

    @Test
    void failDeleteAuth() {
        AuthService.getInstance().deleteAuth(token1);
        assertEquals(401, AuthService.getInstance().deleteAuth(token1).statusCode());
    }

    @Test
    void validate() {
        IServiceResponse res = AuthService.getInstance().validate(token1);
        assertEquals(200, res.statusCode());
        res = AuthService.getInstance().validate(token2);
        assertEquals(200, res.statusCode());
    }

    @Test
    void failValidate() {
        assertEquals(401, AuthService.getInstance().validate(INVALID_AUTH).statusCode());
    }
}