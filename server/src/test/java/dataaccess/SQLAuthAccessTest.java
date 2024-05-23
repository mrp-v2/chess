package dataaccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthAccessTest {

    private static final String USERNAME = "username";
    private String token;

    @Test
    void getInstance() {
        assertNotNull(SQLAuthAccess.getInstance());
    }

    @Test
    void createAuth() {
        assertDoesNotThrow(() -> {
            token = SQLAuthAccess.getInstance().createAuth(USERNAME);
        }, "should not have an error with a new username");
        assertNotNull(token, "should not be null with a new username");
    }

    @Test
    void invalidateFails() {
        assertDoesNotThrow(() -> {
            boolean success = SQLAuthAccess.getInstance().invalidate("invalid");
            assertFalse(success, "should be false with an invalid token");
        }, "should not have an error");
    }

    @Test
    void invalidate() {
        createAuth();
        assertDoesNotThrow(() -> {
            boolean success = SQLAuthAccess.getInstance().invalidate(token);
            assertTrue(success, "should be true with a valid token");
        }, "should not have an error");
    }

    @Test
    void getUserFails() {
        assertDoesNotThrow(() -> {
            String user = SQLAuthAccess.getInstance().getUser("invalid");
            assertNull(user, "should be null with an invalid token");
        }, "should not have an error");
    }

    @Test
    void getUser() {
        createAuth();
        assertDoesNotThrow(() -> {
            String user = SQLAuthAccess.getInstance().getUser(token);
            assertEquals(USERNAME, user, String.format("%s should be %s", user, USERNAME));
        });
    }

    @Test
    @BeforeEach
    @AfterEach
    void clearNoError() {
        assertDoesNotThrow(SQLAuthAccess.getInstance()::clear, "should not have an error");
    }

    @Test
    void clear() {
        createAuth();
        getUser();
        clearNoError();
        assertDoesNotThrow(() -> {
            String user = SQLAuthAccess.getInstance().getUser(USERNAME);
            assertNull(user, "should be null after clearing");
        }, "should not have an error");
    }
}