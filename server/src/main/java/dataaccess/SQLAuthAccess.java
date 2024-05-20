package dataaccess;

public class SQLAuthAccess extends SQLAccess implements AuthAccess {

    private static SQLAuthAccess instance;

    public static SQLAuthAccess getInstance() {
        if (instance == null) {
            instance = new SQLAuthAccess();
        }
        return instance;
    }

    private SQLAuthAccess() {
        try {
            DatabaseManager.createDatabase();
            update("""
                    CREATE TABLE IF NOT EXISTS auth (
                        `token` varchar(256) primary key NOT NULL,
                        `username` varchar(256) NOT NULL
                    );
                    """
            );
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        String token = AuthAccess.makeAuth();
        update("INSERT INTO auth (token, username) VALUES (?, ?)", statement -> {
            statement.setString(1, token);
            statement.setString(2, username);
        });
        return token;
    }

    @Override
    public boolean invalidate(String authToken) throws DataAccessException {
        return update("DELETE FROM auth WHERE token=?", statement -> {
            statement.setString(1, authToken);
        }) == 1;
    }

    @Override
    public String getUser(String authToken) throws DataAccessException {
        return query("SELECT username FROM auth WHERE token=?", statement -> {
            statement.setString(1, authToken);
        }, result -> {
            return result.getString("username");
        });
    }

    @Override
    public void clear() throws DataAccessException {
        update("TRUNCATE auth");
    }
}
