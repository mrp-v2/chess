package dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultTransformer<T> {

    T transform(ResultSet result) throws SQLException;
}
