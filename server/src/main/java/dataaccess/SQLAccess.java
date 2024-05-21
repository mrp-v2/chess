package dataaccess;

import java.sql.*;

public class SQLAccess {

    protected SQLAccess(String tableCreationStatement) {
        try {
            DatabaseManager.createDatabase();
            update(tableCreationStatement);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected int update(String statement) throws DataAccessException {
        return update(statement, ps -> {
        });
    }

    protected int update(String statement, PreparedStatementTransformer transformer) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement prepared = connection.prepareStatement(statement)) {
                transformer.transform(prepared);
                return prepared.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T update(String statement, PreparedStatementTransformer transformer, ResultTransformer<T> generatedKeysTransformer) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement prepared = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                transformer.transform(prepared);
                prepared.executeUpdate();
                try (ResultSet result = prepared.getGeneratedKeys()) {
                    if (result.next()) {
                        return generatedKeysTransformer.transform(result);
                    } else {
                        return null;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T query(String statement, ResultTransformer<T> resultTransformer) throws DataAccessException {
        return query(statement, ps -> {
        }, resultTransformer);
    }

    protected <T> T query(String statement, PreparedStatementTransformer statementTransformer, ResultTransformer<T> resultTransformer) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement prepared = connection.prepareStatement(statement, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statementTransformer.transform(prepared);
                try (ResultSet result = prepared.executeQuery()) {
                    if (result.next()) {
                        return resultTransformer.transform(result);
                    } else {
                        return null;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
