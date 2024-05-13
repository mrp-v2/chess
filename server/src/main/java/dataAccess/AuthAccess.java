package dataAccess;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface AuthAccess {
    String createAuth(String username);

    boolean invalidate(String authToken);

    boolean validate(String authToken);

    String getUser(String authToken) throws DataAccessException;

    void clear();

    class Local implements AuthAccess {

        private static AuthAccess instance;

        public static AuthAccess getInstance() {
            if (instance == null) {
                instance = new Local();
            }
            return instance;
        }

        private final Map<String, String> authTokenUsernames;

        public Local() {
            authTokenUsernames = new HashMap<>();
        }

        @Override
        public String createAuth(String username) {
            String token = UUID.randomUUID().toString();
            authTokenUsernames.put(token, username);
            return token;
        }

        @Override
        public boolean invalidate(String authToken) {
            return authTokenUsernames.remove(authToken) != null;
        }

        @Override
        public boolean validate(String authToken) {
            return authTokenUsernames.containsKey(authToken);
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
