package java.service;

import model.AuthData;
import model.IJsonSerializable;
import model.IServiceResponse;
import org.junit.jupiter.api.Test;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private static final String user1 = "user1";
    private String token1, token2;
    private static final String INVALID_AUTH = "INVALID_AUTH";

    @Test
    void getInstance() {
        assertNotNull(AuthService.getInstance());
    }

    @Test
    void clear() {
        createAuth();
        AuthService.getInstance().clear();
        assertEquals(401, AuthService.getInstance().validate(token1).statusCode());
    }

    @Test
    void createAuth() {
        IJsonSerializable res = AuthService.getInstance().createAuth(user1);
        assertInstanceOf(AuthData.class, res);
        assertEquals(((AuthData) res).username(), user1);
        token1 = ((AuthData) res).authToken();
        assertNotNull(token1);
        res = AuthService.getInstance().createAuth(user1);
        token2 = ((AuthData) res).authToken();
    }

    @Test
    void deleteAuth() {
        createAuth();
        AuthService.getInstance().deleteAuth(token1);
        assertEquals(401, AuthService.getInstance().validate(token1).statusCode());
    }

    @Test
    void validate() {
        createAuth();
        IServiceResponse res = AuthService.getInstance().validate(token1);
        assertEquals(200, res.statusCode());
        res = AuthService.getInstance().validate(token2);
        assertEquals(200, res.statusCode());
        res = AuthService.getInstance().validate(INVALID_AUTH);
        assertEquals(401, res.statusCode());
    }
}