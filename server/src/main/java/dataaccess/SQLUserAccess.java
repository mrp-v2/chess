package dataaccess;

public class SQLUserAccess extends SQLAccess implements UserAccess {

    private static SQLUserAccess instance;

    public static SQLUserAccess getInstance() {
        if (instance == null) {
            instance = new SQLUserAccess();
        }
        return instance;
    }

    protected SQLUserAccess() {
        super("""
                CREATE TABLE IF NOT EXISTS user (
                    `username` varchar(256) primary key NOT NULL,
                    `password` varchar(256) NOT NULL,
                    `email` varchar(256) NOT NULL
                );
                """);
    }

    @Override
    public String getUserPasswordHash(String username) throws DataAccessException {
        return query("SELECT password FROM user WHERE username=?",
                statement -> statement.setString(1, username),
                result -> result.getString("password"));
    }

    @Override
    public boolean createUser(String username, String password, String email) throws DataAccessException {
        if (query("SELECT 1 FROM user WHERE username=?", statement -> {
            statement.setString(1, username);
        }, result -> true) != null) {
            return false;
        } else {
            return update("INSERT INTO user (username, password, email) VALUES (?, ?, ?)", statement -> {
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, email);
            }) == 1;
        }
    }

    @Override
    public void clear() throws DataAccessException {
        update("TRUNCATE user");
    }
}
