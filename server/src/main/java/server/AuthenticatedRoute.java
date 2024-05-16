package server;

import model.ServiceResponse;
import spark.Request;
import spark.Response;

@FunctionalInterface
public interface AuthenticatedRoute {
    /**
     * @param request                The request object providing information about the HTTP request
     * @param response               The response object providing functionality for modifying the response
     * @param authenticationResponse The success response of the authentication
     * @return The content to be set in the response
     */
    Object handle(Request request, Response response, ServiceResponse authenticationResponse);
}
