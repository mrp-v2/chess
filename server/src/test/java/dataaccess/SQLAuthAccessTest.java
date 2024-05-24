package dataaccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthAccessTest extends SQLAccessTest {

    private static final String USERNAME = "username";
    private String token;

    @Test
    void getInstance() {
        assertNotNull(SQLAuthAccess.getInstance());
    }

    @Test
    void createAuth() {
        noError(() -> {
            token = SQLAuthAccess.getInstance().createAuth(USERNAME);
        });
        assertNotNull(token, "should not be null with a new username");
    }

    @Test
    void invalidateFails() {
        noError(() -> {
            boolean success = SQLAuthAccess.getInstance().invalidate("invalid");
            assertFalse(success, "should be false with an invalid token");
        });
    }

    @Test
    void invalidate() {
        createAuth();
        noError(() -> {
            boolean success = SQLAuthAccess.getInstance().invalidate(token);
            assertTrue(success, "should be true with a valid token");
        });
    }

    @Test
    void getUserFails() {
        noError(() -> {
            String user = SQLAuthAccess.getInstance().getUser("invalid");
            assertNull(user, "should be null with an invalid token");
        });
    }

    @Test
    void getUser() {
        createAuth();
        noError(() -> {
            String user = SQLAuthAccess.getInstance().getUser(token);
            assertEquals(USERNAME, user, String.format("%s should be %s", user, USERNAME));
        });
    }

    @Test
    @BeforeEach
    @AfterEach
    void clearNoError() {
        noError(SQLAuthAccess.getInstance()::clear);
    }

    @Test
    void clear() {
        createAuth();
        clearNoError();
        noError(() -> {
            String user = SQLAuthAccess.getInstance().getUser(USERNAME);
            assertNull(user, "should be null after clearing");
        });
    }
}