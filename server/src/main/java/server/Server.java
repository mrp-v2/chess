package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Gson gson = new Gson();

        Spark.staticFiles.location("web");

        Spark.before((req, res) -> {
            switch (req.pathInfo()) {
                case "/session":
                    if (req.requestMethod().equals("post")) {
                        return;
                    }
                    break;
                case "/db":
                case "/user":
                    return;
                case "/game":
                    if (req.requestMethod().equals("put")) {
                        return;
                    }
                    break;
            }
            String token = req.headers("authorization");
            IServiceResponse result = AuthService.getInstance().validate(token);
            if (result.failure()) {
                ErrorModel.UNAUTHORIZED.send(res);
                Spark.halt();
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
            IServiceResponse result = UserService.getInstance().register(data);
            if (result.failure()) {
                res.status(result.statusCode());
                return result.toJson();
            }
            result = AuthService.getInstance().createAuth(data.username());
            res.status(result.statusCode());
            return result.toJson();
        });

        Spark.post("/session", (req, res) -> {
            LoginRequest data;
            try {
                data = gson.fromJson(req.body(), LoginRequest.class);
            } catch (JsonSyntaxException e) {
                return ErrorModel.UNAUTHORIZED.send(res);
            }
            IServiceResponse result = UserService.getInstance().getUser(data);
            if (result.failure()) {
                res.status(result.statusCode());
                return result.toJson();
            }
            result = AuthService.getInstance().createAuth(data.username());
            res.status(result.statusCode());
            return result.toJson();
        });

        Spark.delete("/session", (req, res) -> {
            String token = req.headers("authorization");
            IServiceResponse result = AuthService.getInstance().deleteAuth(token);
            if (result.failure()) {
                res.status(result.statusCode());
                return result.toJson();
            }
            res.status(200);
            return "";
        });

        Spark.get("/game", (req, res) -> {
            IServiceResponse result = GameService.getInstance().getGames();
            res.status(result.statusCode());
            return result.toJson();
        });

        Spark.post("/game", (req, res) -> {
            GameRequest data;
            try {
                data = gson.fromJson(req.body(), GameRequest.class);
            } catch (JsonSyntaxException e) {
                res.status(400);
                return new ErrorResponse("Error: bad request").toJson();
            }
            IServiceResponse result = GameService.getInstance().create(data);
            res.status(result.statusCode());
            return result.toJson();
        });

        Spark.put("/game", (req, res) -> {
            String token = req.headers("authorization");
            IServiceResponse result = AuthService.getInstance().validate(token);
            if (result.failure()) {
                return ErrorModel.UNAUTHORIZED.send(res);
            }
            JoinGameRequest data;
            try {
                data = gson.fromJson(req.body(), JoinGameRequest.class);
            } catch (JsonSyntaxException e) {
                res.status(400);
                return new ErrorResponse("Error: bad request").toJson();
            }
            result = GameService.getInstance().join(data, ((UserResponse) result).username());
            res.status(result.statusCode());
            return result.toJson();
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
