package dataAccess;

import model.UserData;

import java.util.*;

public interface UserAccess {
    UserData getUser(String username, String password);

    boolean createUser(UserData data);

    void clear();

    class Local implements UserAccess {

        private static UserAccess instance;

        public static UserAccess getInstance() {
            if (instance == null) {
                instance = new Local();
            }
            return instance;
        }

        private final Map<String, UserData> users;

        public Local() {
            users = new HashMap<>();
        }

        @Override
        public UserData getUser(String username, String password) {
            UserData data = users.get(username);
            if (data == null) {
                return null;
            }
            else if (data.password().equals(password)) {
                return data;
            } else {
                return null;
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
