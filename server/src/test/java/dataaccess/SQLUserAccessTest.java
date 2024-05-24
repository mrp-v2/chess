package dataaccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserAccessTest extends SQLAccessTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "my@email.com";

    @Test
    void getInstance() {
        assertNotNull(SQLUserAccess.getInstance());
    }

    @Test
    void getUserPasswordHash() {
        createUser();
        noError(() -> {
            String hash = SQLUserAccess.getInstance().getUserPasswordHash(USERNAME);
            assertNotNull(hash);
        });
    }

    @Test
    void getUserPasswordHashFails() {
        noError(() -> {
            String hash = SQLUserAccess.getInstance().getUserPasswordHash(USERNAME);
            assertNull(hash);
        });
    }

    @Test
    void createUser() {
        noError(() -> {
            boolean success = SQLUserAccess.getInstance().createUser(USERNAME, PASSWORD, EMAIL);
            assertTrue(success, "should be true with a new user");
        });
    }

    @Test
    void createUserFails() {
        createUser();
        noError(() -> {
            boolean success = SQLUserAccess.getInstance().createUser(USERNAME, PASSWORD, EMAIL);
            assertFalse(success, "should be false with an existing user");
        });
    }

    @BeforeEach
    @AfterEach
    @Test
    void clearNoError() {
        noError(SQLUserAccess.getInstance()::clear);
    }

    @Test
    void clear(){
        createUser();
        clearNoError();
        getUserPasswordHashFails();
    }
}