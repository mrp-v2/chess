package model;

import javax.naming.OperationNotSupportedException;

public record GameResponse(int gameID) implements IServiceResponse {
    @Override
    public boolean failure() {
        return false;
    }

    @Override
    public int statusCode() throws OperationNotSupportedException {
        return 200;
    }
}
