package model;

import javax.naming.OperationNotSupportedException;
import java.util.Collection;

public record GamesData(Collection<GameData> games) implements IServiceResponse {
    @Override
    public boolean failure() {
        return false;
    }

    @Override
    public int statusCode() throws OperationNotSupportedException {
        return 200;
    }
}
