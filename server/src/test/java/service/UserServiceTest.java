package service;

import model.LoginRequest;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    public static final UserData USER_1 = new UserData("user1", "password1", "email1");
    public static final LoginRequest LOGIN_1 = new LoginRequest(USER_1.username(), USER_1.password());

    @AfterEach
    @BeforeEach
    void cleanup() {
        UserService.getInstance().clear();
    }

    @Test
    void getInstance() {
        assertNotNull(UserService.getInstance());
    }

    @Test
    void register() {
        assertEquals(200, UserService.getInstance().register(USER_1).statusCode());
    }

    @Test
    void failRegister() {
        assertEquals(400, UserService.getInstance().register(new UserData("user", null, "email")).statusCode());
    }

    @Test
    void clear() {
        assertEquals(200, UserService.getInstance().clear().statusCode());
        register();
        assertEquals(200, UserService.getInstance().clear().statusCode());
        assertEquals(401, UserService.getInstance().createUserAuth(LOGIN_1).statusCode());
    }
}