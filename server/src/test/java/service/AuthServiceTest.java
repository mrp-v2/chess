package service;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

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
        ServiceResponse res = UserService.getInstance().register(UserServiceTest.USER_1);
        AuthResponse data = (AuthResponse) res.data();
        token1 = data.authToken();
        res = UserService.getInstance().createUserAuth(UserServiceTest.LOGIN_1);
        token2 = ((AuthResponse) res.data()).authToken();
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
        ServiceResponse res = AuthService.getInstance().validate(token1);
        assertEquals(200, res.statusCode());
        res = AuthService.getInstance().validate(token2);
        assertEquals(200, res.statusCode());
    }

    @Test
    void failValidate() {
        assertEquals(401, AuthService.getInstance().validate(INVALID_AUTH).statusCode());
    }
}