package dataaccess;

import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class SQLAccessTest {

    void noError(Executable function) {
        assertDoesNotThrow(function, "should not have an error");
    }
}