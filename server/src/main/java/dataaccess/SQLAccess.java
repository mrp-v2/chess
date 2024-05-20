package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAccess {

    protected int update(String statement) throws DataAccessException {
        return update(statement, (ps) -> {
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

    protected <T> T query(String statement, PreparedStatementTransformer statementTransformer, ResultTransformer<T> resultTransformer) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement prepared = connection.prepareStatement(statement)) {
                statementTransformer.transform(prepared);
                try (ResultSet result = prepared.executeQuery()) {
                    if (result.next()) {
                        return resultTransformer.transform(result);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
