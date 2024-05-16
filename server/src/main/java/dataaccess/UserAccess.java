package dataaccess;

import model.UserData;

import java.util.*;

public interface UserAccess {
    /**
     * Verifies a users credentials.
     */
    boolean validateUser(String username, String password);

    boolean createUser(UserData data);

    /**
     * Deletes all users.
     */
    void clear();

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
        public boolean validateUser(String username, String password) {
            UserData data = users.get(username);
            if (data == null) {
                return false;
            } else {
                return data.password().equals(password);
            }
        }

        @Override
        public boolean createUser(UserData data) {
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
