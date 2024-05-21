package dataaccess;

import model.UserData;

import java.util.*;

public interface UserAccess {
    /**
     * Verifies a users credentials.
     */
    String getUserPasswordHash(String username) throws DataAccessException;

    boolean createUser(String username, String password, String email) throws DataAccessException;

    /**
     * Deletes all users.
     */
    void clear() throws DataAccessException;

    /**
     * Implements {@link UserAccess} using RAM.
     */
    class Local implements UserAccess {

        private static UserAccess instance;

        /**
         * Returns a reference to the global {@link Local} instance.
         */
        public static UserAccess getInstance() {
            if (instance == null) {
                instance = new Local();
            }
            return instance;
        }

        private final Map<String, UserData> users;

        private Local() {
            users = new HashMap<>();
        }

        @Override
        public String getUserPasswordHash(String username) {
            UserData data = users.get(username);
            if (data == null) {
                return null;
            } else {
                return data.password();
            }
        }

        @Override
        public boolean createUser(String username, String password, String email) {
            UserData data = new UserData(username, password, email);
            if (users.containsKey(data.username())) {
                return false;
            }
            users.put(data.username(), data);
            return true;
        }

        @Override
        public void clear() {
            users.clear();
        }
    }
}
