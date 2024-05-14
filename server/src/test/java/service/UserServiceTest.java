package service;

import model.LoginRequest;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    public static final UserData user1 = new UserData("user1", "password1", "email1");
    public static final LoginRequest login1 = new LoginRequest(user1.username(), user1.password());

    @AfterEach
    void cleanup() {
        UserService.getInstance().clear();
    }

    @Test
    void getInstance() {
        assertNotNull(UserService.getInstance());
    }

    @Test
    void register() {
        assertEquals(200, UserService.getInstance().register(user1).statusCode());
    }

    @Test
    void failRegister() {
        assertEquals(400, UserService.getInstance().register(new UserData("user", null, "email")).statusCode());
    }

    @Test
    void clear() {
        register();
        UserService.getInstance().clear();
        assertEquals(401, UserService.getInstance().createUserAuth(login1).statusCode());
    }
}