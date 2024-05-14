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
            IServiceResponse result = AuthService.getInstance().validate(token);
            if (result.failure()) {
                Spark.halt(result.statusCode(), result.toJson());
            }
        });

        Spark.delete("/db", (req, res) -> {
            UserService.getInstance().clear();
            AuthService.getInstance().clear();
            GameService.getInstance().clear();
            res.status(200);
            return "";
        });

        Spark.post("/user", (req, res) -> {
            UserData data;
            try {
                data = gson.fromJson(req.body(), UserData.class);
            } catch (JsonSyntaxException e) {
                res.status(400);
                return new ErrorResponse("Error: bad request").toJson();
            }
            return UserService.getInstance().register(data).send(res);
        });

        Spark.post("/session", (req, res) -> {
            LoginRequest data;
            try {
                data = gson.fromJson(req.body(), LoginRequest.class);
            } catch (JsonSyntaxException e) {
                return ErrorModel.UNAUTHORIZED.send(res);
            }
            return UserService.getInstance().createUserAuth(data).send(res);
        });

        Spark.delete("/session", (req, res) -> {
            String token = req.headers("authorization");
            IServiceResponse result = AuthService.getInstance().deleteAuth(token);
            if (result.failure()) {
                return result.send(res);
            }
            return IServiceResponse.SUCCESS.send(res);
        });

        Spark.get("/game", (req, res) -> {
            return GameService.getInstance().getGames().send(res);
        });

        Spark.post("/game", (req, res) -> {
            GameRequest data;
            try {
                data = gson.fromJson(req.body(), GameRequest.class);
            } catch (JsonSyntaxException e) {
                res.status(400);
                return new ErrorResponse("Error: bad request").toJson();
            }
            return GameService.getInstance().create(data).send(res);
        });

        Spark.put("/game", (req, res) -> {
            String token = req.headers("authorization");
            IServiceResponse result = AuthService.getInstance().validate(token);
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
