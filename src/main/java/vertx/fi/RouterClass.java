package vertx.fi;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Created by joe on 16/7/24.
 */
public class RouterClass {

    public static String userr="root";
    public static String pass="123";
    public static String url="jdbc:mysql://localhost:3306/team3?useUnicode=true&characterEncoding=utf-8";


    /**
     * Instantiates a new RouterClass. Private to prevent instantiation.
     */
    private RouterClass() {

        // Throw an exception if this ever *is* called
        throw new AssertionError("Instantiating utility class.");
    }


    /**
     * amazon search method
     *
     * @param routingContext receives routing context from vertx.
     */
    static void searchProducts(final RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();

        /**
         * use hash map or a class object for response, and u can also use arraylist store some object.
         */

        HashMap<String, String> response = new HashMap<>();
        String url = null;

        try {
            SignedRequestsHelper encrpt = new SignedRequestsHelper();
            HashMap<String, String> hmap = new HashMap<String, String>();

            String AssociateTag = System.getenv("ASSOCIATE_TAG");

            hmap.put("AssociateTag", AssociateTag);

            String Operation = "ItemSearch";

            hmap.put("Operation", Operation);

            String Service = "AWSECommerceService";

            hmap.put("Service", Service);


            String Keywords = "the hunger games";

            hmap.put("Keywords", Keywords);

            String SearchIndex = "Books";

            hmap.put("SearchIndex", SearchIndex);

            String ResponseGroup = "Images,ItemAttributes,Accessories, EditorialReview, Reviews";

            hmap.put("ResponseGroup", ResponseGroup);

            String Sort = "relevancerank";

            hmap.put("Sort", Sort);

            //String ItemPage = "1";

            //hmap.put("ItemPage", ItemPage);

            //String Version = "2015-08-01";

            //hmap.put("Version", Version);

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
     * amazon method
     *
     * @param routingContext receives routing context from vertx.
     */
    static void amazonsProduct(final RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();

        /**
         * use hash map or a class object for response, and u can also use arraylist store some object.
         */

        HashMap<String, String> response = new HashMap<>();
        String url = null;

        try {
            SignedRequestsHelper encrpt = new SignedRequestsHelper();
            HashMap<String, String> hmap = new HashMap<String, String>();

            String AssociateTag = System.getenv("ASSOCIATE_TAG");

            hmap.put("AssociateTag", AssociateTag);

            String Operation = "ItemLookup";

            hmap.put("Operation", Operation);

            String Service = "AWSECommerceService";

            hmap.put("Service", Service);

            String ItemId = "0976925524";

            hmap.put("ItemId", ItemId);

            String ResponseGroup = "Images,ItemAttributes,Accessories, EditorialReview, Reviews";

            hmap.put("ResponseGroup", ResponseGroup);

            String Version = "2015-08-01";

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
     * navigation method
     *
     * @param routingContext receives routing context from vertx.
     */
    static void beaconList(final RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();

        /**
         * use hash map or a class object for response, and u can also use arraylist store some object.
         */
        HashMap<String, String> response = new HashMap<>();


        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(url, userr, pass);
            Statement stmt = con.createStatement();



            String sql="select * from beacon_list order by idbeacon_list";

            ResultSet rs=stmt.executeQuery(sql);
            int i = 0;
            while(rs.next()) {

                if (i == 0) {
                    response.put("idA", rs.getString("beacon_id"));
                } else if (i == 1) {
                    response.put("idB", rs.getString("beacon_id"));
                } else if (i == 2) {
                    response.put("idC", rs.getString("beacon_id"));
                } else {
                    System.out.println("no use id");
                    break;
                }
                i++;
            }


            rs.close();
            stmt.close();
            con.close();
        } catch(Exception e) {
            e.printStackTrace();
        }


        returnResponse(routingContext, 200, response);
        return;
    }


    /**
     * navigation method
     *
     * @param routingContext receives routing context from vertx.
     */
    static void navigation(final RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();

        /**
         * use hash map or a class object for response, and u can also use arraylist store some object.
         */
        HashMap<String, String> response = new HashMap<>();


        String rssiA = request.getParam("rssiA");

        String txPowerA = request.getParam("txPowerA");


        double distanceA = getDistance(Integer.parseInt(rssiA), Integer.parseInt(txPowerA))/10.0;

        System.out.println("distanceA " + String.valueOf(distanceA));


        String rssiB = request.getParam("rssiB");

        String txPowerB = request.getParam("txPowerB");

        double distanceB = getDistance(Integer.parseInt(rssiB), Integer.parseInt(txPowerB))/10.0;

        System.out.println("distanceB " + String.valueOf(distanceB));



        String rssiC = request.getParam("rssiC");

        String txPowerC = request.getParam("txPowerC");

        double distanceC = getDistance(Integer.parseInt(rssiC), Integer.parseInt(txPowerC))/10.0;

        System.out.println("distanceC " + String.valueOf(distanceC));

        Pointer xy = getPointer(distanceA, distanceB, distanceC);

        response.put("x", String.valueOf(xy.x));

        response.put("y", String.valueOf(xy.y));


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

    private static double getDistance(int rssi, int txPower) {
    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     *
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */
        return Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
    }

    private static Pointer getPointer(double dA, double dB, double dC) {

        double x = 0, y = 0;
        Pointer a = new Pointer(0, 0),b = new Pointer(0, 0),c = new Pointer(0, 0);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(url, userr, pass);
            Statement stmt = con.createStatement();



            String sql="select * from beacon_list order by idbeacon_list";

            ResultSet rs=stmt.executeQuery(sql);
            int i = 0;
            while(rs.next()) {
                double tempX = rs.getDouble("x");
                double tempY = rs.getDouble("y");

                if (i == 0) {
                    a = new Pointer(tempX, tempY);
                    System.out.println(rs.getString("beacon_id") + " "
                            + String.valueOf(tempX) + " " + String.valueOf(tempY));
                } else if (i == 1) {
                    b = new Pointer(tempX, tempY);
                    System.out.println(rs.getString("beacon_id") + " "
                            + String.valueOf(tempX) + " " + String.valueOf(tempY));
                } else if (i == 2) {
                    c = new Pointer(tempX, tempY);
                    System.out.println(rs.getString("beacon_id") + " "
                            + String.valueOf(tempX) + " " + String.valueOf(tempY));
                } else {
                    System.out.println("no use id");
                    break;
                }
                i++;
            }

            double W = dA*dA - dB*dB - a.x*a.x - a.y*a.y + b.x*b.x + b.y*b.y;
            double Z = dB*dB - dC*dC - b.x*b.x - b.y*b.y + c.x*c.x + c.y*c.y;

            x = (W*(c.y-b.y) - Z*(b.y-a.y)) / (2 * ((b.x-a.x)*(c.y-b.y) - (c.x-b.x)*(b.y-a.y)));
            y = (W - 2*x*(b.x-a.x)) / (2*(b.y-a.y));
            //y2 is a second measure of y to mitigate errors
            double y2 = (Z - 2*x*(c.x-b.x)) / (2*(c.y-b.y));

            y = (y + y2) / 2;


            rs.close();
            stmt.close();
            con.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return new Pointer(x,y);
    }


}
