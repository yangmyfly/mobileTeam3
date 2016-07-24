package vertx.fi;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;

/**
 * Created by joe on 16/7/24.
 */
public class RouterClass {

    /**
     * Instantiates a new RouterClass. Private to prevent instantiation.
     */
    private RouterClass() {

        // Throw an exception if this ever *is* called
        throw new AssertionError("Instantiating utility class.");
    }

    /**
     * Login method responding to the post action.
     *
     * @param routingContext receives routing context from vertx.
     */
    static void amazonsProduct(final RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();

        /**
         * use hash map or a class object, and u can also use arraylist store some object.
         */

        HashMap<String, String> response = new HashMap<>();
        response.put("error", "no data");
        /*
        User user = new User(request.getParam("username"),
                request.getParam("password"));
        boolean validUser = user.isValidUser(User.getFileName());
        HashMap<String, String> response = new HashMap<>();
        int responseCode;
        if (validUser) {
            response.put("Token", user.getApiToken());
            responseCode = SUCCESS;
        } else {
            response.put("error", "Invalid combination of username and "
                    + "password.");
            responseCode = ERROR;
        }
        */
        returnResponse(routingContext, 200, response);
        return;
    }

    /**
     * A Helper method to respond to request.
     *
     * @param routingContext Routing context object from vertx
     * @param responseCode   response code to send
     * @param response       response message to send
     */
    private static void returnResponse(final RoutingContext routingContext, int
            responseCode, Object response) {
        routingContext.response()
                .setStatusCode(responseCode)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(response));
    }


}
