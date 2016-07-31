package vertx.fi;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Created by joe on 16/7/24.
 */
/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
final class MainServer {
    /**
     * Variable for port number.
     */
    private static final int PORT = 8080;

    /**
     * Instantiates a new MainServer. Private to prevent instantiation
     */
    private MainServer() {

        // Throw an exception if this ever *is* called
        throw new AssertionError("Instantiating utility class.");
    }

    /**
     * Main method initiating the vertx web server.
     *
     * @param args standard arguments
     */
    public static void main(final String[] args) {
        // Create an HTTP server which simply returns "Hello World!" to each
        // request.
        Router router = Router.router(Vertx.vertx());

        BodyHandler bodyHandler = BodyHandler.create();
        //bodyHandler.setUploadsDirectory("faults");
        router.route().handler(bodyHandler);

        router.route("/test").blockingHandler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain");
            response.end("yes");
        }, false);

        //router.post("/login").blockingHandler(RouterClass::login, false);

        router.get("/amazonsProduct").blockingHandler(RouterClass::amazonsProduct, false);

        router.get("/beaconList").blockingHandler(RouterClass::beaconList, false);

        router.post("/navigation").blockingHandler(RouterClass::navigation, false);

        router.get("/searchProducts").blockingHandler(RouterClass::searchProducts, false);

        router.get("/getProductLoc").blockingHandler(RouterClass::getProductLoc, false);

        router.get("/commonPlace").blockingHandler(RouterClass::commonPlace, false);

        // get all shopping lists
        router.get("/shoppingLists").blockingHandler(UserSpecified::shoppingLists, false);
        // add or update a shopping list
        router.post("/editShoppingList").blockingHandler(UserSpecified::editShoppingList, false);
        // get all coupons
        router.get("/coupons").blockingHandler(UserSpecified::coupons, false);
        // add or remove a coupon
        router.post("/editCoupon").blockingHandler(UserSpecified::editCoupon, false);
        // get user profile
        router.get("/profile").blockingHandler(UserSpecified::profile, false);
        // edit user profile (set membership card id, etc)
        router.post("/editProfile").blockingHandler(UserSpecified::editProfile, false);
        // get recommended products based on Machine Learning algorithms
        router.get("/recommendations").blockingHandler(UserSpecified::recommendations, false);
        // add browsing history for the server to analyze data, e.g. train Machine Learning model
        router.post("/history").blockingHandler(UserSpecified::history, false);
        
        Vertx.vertx().createHttpServer().requestHandler(router::accept)
                .listen(PORT);
    }

}