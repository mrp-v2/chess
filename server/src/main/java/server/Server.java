package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {

    private static final Gson GSON = new Gson();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        registerEndpoints();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void registerEndpoints() {
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::getGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
    }

    private Object clear(Request req, Response res) {
        ServiceResponse result = UserService.getInstance().clear();
        if (result.failure()) {
            return result.send(res);
        }
        result = AuthService.getInstance().clear();
        if (result.failure()) {
            return result.send(res);
        }
        return GameService.getInstance().clear().send(res);
    }

    private Object register(Request req, Response res) {
        UserData data;
        try {
            data = GSON.fromJson(req.body(), UserData.class);
        } catch (JsonSyntaxException e) {
            return ErrorModel.BAD_REQUEST.send(res);
        }
        return UserService.getInstance().register(data).send(res);
    }

    private Object login(Request req, Response res) {
        LoginRequest data;
        try {
            data = GSON.fromJson(req.body(), LoginRequest.class);
        } catch (JsonSyntaxException e) {
            return ErrorModel.UNAUTHORIZED.send(res);
        }
        return UserService.getInstance().createUserAuth(data).send(res);
    }

    private Object logout(Request req, Response res) {
        String token = req.headers("authorization");
        ServiceResponse result = AuthService.getInstance().deleteAuth(token);
        if (result.failure()) {
            return result.send(res);
        }
        return ServiceResponse.SUCCESS.send(res);
    }

    private Object getGames(Request req, Response res) {
        return authWrap(req, res, this::authenticatedGetGames);
    }

    private Object authenticatedGetGames(Request req, Response res, ServiceResponse auth) {
        return GameService.getInstance().getGames().send(res);
    }

    private Object createGame(Request req, Response res) {
        return authWrap(req, res, this::authenticatedCreateGame);
    }

    private Object authenticatedCreateGame(Request req, Response res, ServiceResponse auth) {
        GameRequest data;
        try {
            data = GSON.fromJson(req.body(), GameRequest.class);
        } catch (JsonSyntaxException e) {
            return ErrorModel.BAD_REQUEST.send(res);
        }
        return GameService.getInstance().create(data).send(res);
    }

    private Object joinGame(Request req, Response res) {
        return authWrap(req, res, this::authenticatedJoinGame);
    }

    private Object authenticatedJoinGame(Request req, Response res, ServiceResponse auth) {
        JoinGameRequest data;
        try {
            data = GSON.fromJson(req.body(), JoinGameRequest.class);
        } catch (JsonSyntaxException e) {
            return ErrorModel.BAD_REQUEST.send(res);
        }
        return GameService.getInstance().join(data, ((UserResponse) auth.data()).username()).send(res);
    }

    /**
     * Verifies that a request is authorized before propagating to an {@link AuthenticatedRoute}.
     */
    private Object authWrap(Request req, Response res, AuthenticatedRoute route) {
        String token = req.headers("authorization");
        ServiceResponse result = AuthService.getInstance().validate(token);
        if (result.failure()) {
            return result.send(res);
        } else {
            return route.handle(req, res, result);
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
