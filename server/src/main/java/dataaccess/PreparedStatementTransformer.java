package dataaccess;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementTransformer {

    void transform(PreparedStatement statement) throws SQLException;
}
