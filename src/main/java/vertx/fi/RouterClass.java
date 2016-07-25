package vertx.fi;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
     * amazon method
     *
     * @param routingContext receives routing context from vertx.
     */
    static void amazonsProduct(final RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();

        /**
         * use hash map or a class object, and u can also use arraylist store some object.
         */

        HashMap<String, String> response = new HashMap<>();
        String url = null;

        try {
            SignedRequestsHelper encrpt = new SignedRequestsHelper();
            HashMap<String, String> hmap = new HashMap<String, String>();

            String AssociateTag = System.getenv("ASSOCIATE_TAG");
            System.out.println(AssociateTag);
            
            hmap.put("AssociateTag", AssociateTag);

            String Operation = "ItemLookup";

            hmap.put("Operation", Operation);

            String Service = "AWSECommerceService";

            hmap.put("Service", Service);

            String ItemId = "0679722769";

            hmap.put("ItemId", ItemId);

            String ResponseGroup = "Images,ItemAttributes,Offers,Reviews";

            hmap.put("ResponseGroup", ResponseGroup);

            String Version = "2013-08-01";

            hmap.put("Version", Version);

            url = encrpt.sign(hmap);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        response.put("url", url);
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
