package dataaccess;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface AuthAccess {
    /**
     * Creates an authentication token for a username.
     */
    String createAuth(String username) throws DataAccessException;

    /**
     * Deletes an authentication token.
     */
    boolean invalidate(String authToken) throws DataAccessException;

    /**
     * Gets the username associated with an authentication token.
     *
     * @throws DataAccessException the token is invalid
     */
    String getUser(String authToken) throws DataAccessException;

    /**
     * Deletes all authentication tokens.
     */
    void clear() throws DataAccessException;

    static String makeAuth() {
        return UUID.randomUUID().toString();
    }

    /**
     * Implements {@link AuthAccess} using RAM.
     */
    class Local implements AuthAccess {

        private static AuthAccess instance;

        /**
         * Returns a reference to the global {@link Local} instance.
         */
        public static AuthAccess getInstance() {
            if (instance == null) {
                instance = new Local();
            }
            return instance;
        }

        private final Map<String, String> authTokenUsernames;

        private Local() {
            authTokenUsernames = new HashMap<>();
        }

        @Override
        public String createAuth(String username) {
            String token = AuthAccess.makeAuth();
            authTokenUsernames.put(token, username);
            return token;
        }

        @Override
        public boolean invalidate(String authToken) {
            return authTokenUsernames.remove(authToken) != null;
        }

        @Override
        public String getUser(String authToken) throws DataAccessException {
            if (!authTokenUsernames.containsKey(authToken)) {
                throw new DataAccessException("invalid authToken");
            }
            return authTokenUsernames.get(authToken);
        }

        @Override
        public void clear() {
            authTokenUsernames.clear();
        }
    }
}
