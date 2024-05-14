package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Spark;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Gson gson = new Gson();

        Spark.staticFiles.location("web");

        // common validation of authentication for relevant endpoints
        Spark.before((req, res) -> {
            switch (req.pathInfo()) {
                case "/session":
                    if (req.requestMethod().equals("POST")) {
                        return;
                    }
                    break;
                case "/db":
                case "/user":
                    return;
                case "/game":
                    if (req.requestMethod().equals("PUT")) {
                        return;
                    }
                    break;
            }
            String token = req.headers("authorization");
            ServiceResponse result = AuthService.getInstance().validate(token);
            if (result.failure()) {
                Spark.halt(result.statusCode(), result.toJson());
            }
        });

        // clear
        Spark.delete("/db", (req, res) -> {
            UserService.getInstance().clear();
            AuthService.getInstance().clear();
            GameService.getInstance().clear();
            res.status(200);
            return "";
        });

        // register user
        Spark.post("/user", (req, res) -> {
            UserData data;
            try {
                data = gson.fromJson(req.body(), UserData.class);
            } catch (JsonSyntaxException e) {
                return ErrorModel.BAD_REQUEST.send(res);
            }
            return UserService.getInstance().register(data).send(res);
        });

        // login user
        Spark.post("/session", (req, res) -> {
            LoginRequest data;
            try {
                data = gson.fromJson(req.body(), LoginRequest.class);
            } catch (JsonSyntaxException e) {
                return ErrorModel.UNAUTHORIZED.send(res);
            }
            return UserService.getInstance().createUserAuth(data).send(res);
        });

        // logout user
        Spark.delete("/session", (req, res) -> {
            String token = req.headers("authorization");
            ServiceResponse result = AuthService.getInstance().deleteAuth(token);
            if (result.failure()) {
                return result.send(res);
            }
            return ServiceResponse.SUCCESS.send(res);
        });

        // get games
        Spark.get("/game", (req, res) -> {
            return GameService.getInstance().getGames().send(res);
        });

        // create game
        Spark.post("/game", (req, res) -> {
            GameRequest data;
            try {
                data = gson.fromJson(req.body(), GameRequest.class);
            } catch (JsonSyntaxException e) {
                return ErrorModel.BAD_REQUEST.send(res);
            }
            return GameService.getInstance().create(data).send(res);
        });

        // join game
        Spark.put("/game", (req, res) -> {
            String token = req.headers("authorization");
            ServiceResponse result = AuthService.getInstance().validate(token);
            if (result.failure()) {
                return result.send(res);
            }
            JoinGameRequest data;
            try {
                data = gson.fromJson(req.body(), JoinGameRequest.class);
            } catch (JsonSyntaxException e) {
                return ErrorModel.BAD_REQUEST.send(res);
            }
            return GameService.getInstance().join(data, ((UserResponse) result.data()).username()).send(res);
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
