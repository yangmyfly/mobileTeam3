package vertx.fi;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.List;

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

        String url = null, rawString = null;

        List<Products> searchList = new ArrayList<>();

        try {
            SignedRequestsHelper encrpt = new SignedRequestsHelper();
            HashMap<String, String> hmap = new HashMap<String, String>();

            String AssociateTag = System.getenv("ASSOCIATE_TAG");

            hmap.put("AssociateTag", AssociateTag);

            String Operation = "ItemSearch";

            hmap.put("Operation", Operation);

            String Service = "AWSECommerceService";

            hmap.put("Service", Service);


            String Keywords = request.getParam("Keywords");

            hmap.put("Keywords", Keywords);

            String SearchIndex = request.getParam("SearchIndex");;

            hmap.put("SearchIndex", SearchIndex);

            String ResponseGroup = "Images,ItemAttributes,Accessories, EditorialReview, Reviews";

            hmap.put("ResponseGroup", ResponseGroup);

            if (SearchIndex.compareTo("All") != 0 && SearchIndex.compareTo("Blended") != 0 && SearchIndex.compareTo("Electronics") != 0) {
                String Sort = "relevancerank";

                hmap.put("Sort", Sort);
            }

            url = encrpt.sign(hmap);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("url " + url);
            rawString = URLHandler(url);

            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            StringBuilder xmlStringBuilder = new StringBuilder();

            xmlStringBuilder.append(rawString);

            ByteArrayInputStream input =  new ByteArrayInputStream(
                    xmlStringBuilder.toString().getBytes("UTF-8"));
            Document doc = dBuilder.parse(input);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :"
                    + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("Item");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :"
                        + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String ASIN, title = "unknown", image = "unknown", price = "unknown", Product_description = "unknown", customerReviews = "unknown";

                    ASIN = eElement
                            .getElementsByTagName("ASIN")
                            .item(0)
                            .getTextContent();

                    System.out.println("ASIN : "
                            + ASIN);


                    Node t1 = eElement
                            .getElementsByTagName("ItemAttributes")
                            .item(0);
                    if (t1 != null) {
                        Element e1 = (Element) t1;
                        title = e1.getElementsByTagName("Title")
                                .item(0)
                                .getTextContent();
                    }

                    System.out.println("Title : "
                            + title);


                    Node m1 = eElement
                            .getElementsByTagName("MediumImage")
                            .item(0);

                    if (m1 != null) {
                        Element e1 = (Element) m1;
                        image = e1.getElementsByTagName("URL")
                                .item(0)
                                .getTextContent();
                    }

                    System.out.println("MediumImage : "
                            + image);

                    Node n1 = eElement
                            .getElementsByTagName("ItemAttributes")
                            .item(0);

                    if (n1 != null) {
                        Element e1 = (Element) n1;
                        Node n2 = e1.getElementsByTagName("ListPrice").item(0);

                        if (n2 != null) {
                            Element e2 = (Element) n2;
                            price = e2.getElementsByTagName("FormattedPrice").item(0).getTextContent();
                        }
                    }


                    System.out.println("price : "
                            +  price);


                    Node r1 = eElement
                            .getElementsByTagName("EditorialReviews")
                            .item(0);

                    if (r1 != null) {
                        Element e1 = (Element) r1;
                        Node r2 = e1.getElementsByTagName("EditorialReview").item(0);

                        if (r2 != null) {
                            Element e2 = (Element) r2;
                            Product_description = e2.getElementsByTagName("Content")
                                    .item(0)
                                    .getTextContent();
                        }
                    }

                    System.out.println("Product Description : "
                            +  Product_description);


                    Node c1 = eElement
                            .getElementsByTagName("CustomerReviews")
                            .item(0);

                    if (c1 != null) {
                        Element e1 = (Element) c1;
                        customerReviews = e1.getElementsByTagName("IFrameURL").item(0).getTextContent();

                    }

                    System.out.println("CustomerReviews : "
                            + customerReviews);

                    searchList.add(new Products(0,0,title, ASIN, price, image, Product_description, customerReviews));

                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }




//        routingContext.response()
//                .setStatusCode(200)
//                .putHeader("content-type", "application/xml; charset=utf-8")
//                .end(rawString);
//        return;


        returnResponse(routingContext, 200, searchList);
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

            String ItemId = request.getParam("ASIN");;

            hmap.put("ItemId", ItemId);

            String ResponseGroup = "Images,ItemAttributes,Accessories, EditorialReview, Reviews";

            hmap.put("ResponseGroup", ResponseGroup);

//            String Version = "2015-08-01";
//
//            hmap.put("Version", Version);

            url = encrpt.sign(hmap);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        //response.put("url", url);

        try {
            System.out.println("url " + url);
            String rawString = URLHandler(url);

            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            StringBuilder xmlStringBuilder = new StringBuilder();

            xmlStringBuilder.append(rawString);

            ByteArrayInputStream input =  new ByteArrayInputStream(
                    xmlStringBuilder.toString().getBytes("UTF-8"));
            Document doc = dBuilder.parse(input);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :"
                    + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("Item");

            Node nNode = nList.item(0);

            System.out.println("\nCurrent Element :"
                    + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                String ASIN, title = "unknown", image = "unknown", price = "unknown", Product_description = "unknown", customerReviews = "unknown";

                ASIN = eElement
                        .getElementsByTagName("ASIN")
                        .item(0)
                        .getTextContent();

                System.out.println("ASIN : "
                        + ASIN);


                Node t1 = eElement
                        .getElementsByTagName("ItemAttributes")
                        .item(0);
                if (t1 != null) {
                    Element e1 = (Element) t1;
                    title = e1.getElementsByTagName("Title")
                            .item(0)
                            .getTextContent();
                }

                System.out.println("Title : "
                        + title);


                Node m1 = eElement
                        .getElementsByTagName("MediumImage")
                        .item(0);

                if (m1 != null) {
                    Element e1 = (Element) m1;
                    image = e1.getElementsByTagName("URL")
                            .item(0)
                            .getTextContent();
                }

                System.out.println("MediumImage : "
                        + image);

                Node n1 = eElement
                        .getElementsByTagName("ItemAttributes")
                        .item(0);

                if (n1 != null) {
                    Element e1 = (Element) n1;
                    Node n2 = e1.getElementsByTagName("ListPrice").item(0);

                    if (n2 != null) {
                        Element e2 = (Element) n2;
                        price = e2.getElementsByTagName("FormattedPrice").item(0).getTextContent();
                    }
                }


                System.out.println("price : "
                        + price);


                Node r1 = eElement
                        .getElementsByTagName("EditorialReviews")
                        .item(0);

                if (r1 != null) {
                    Element e1 = (Element) r1;
                    Node r2 = e1.getElementsByTagName("EditorialReview").item(0);

                    if (r2 != null) {
                        Element e2 = (Element) r2;
                        Product_description = e2.getElementsByTagName("Content")
                                .item(0)
                                .getTextContent();
                    }
                }

                System.out.println("Product Description : "
                        + Product_description);


                Node c1 = eElement
                        .getElementsByTagName("CustomerReviews")
                        .item(0);

                if (c1 != null) {
                    Element e1 = (Element) c1;
                    customerReviews = e1.getElementsByTagName("IFrameURL").item(0).getTextContent();

                }

                System.out.println("CustomerReviews : "
                        + customerReviews);

                //new Products(0,0,title, ASIN, price, image, Product_description, customerReviews);
                response.put("x", "0");
                response.put("y", "0");
                response.put("title", title);
                response.put("ASIN", ASIN);
                response.put("price", price);
                response.put("image", image);
                response.put("description", Product_description);
                response.put("customReview", customerReviews);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        returnResponse(routingContext, 200, response);
        return;
    }

    /**
     * beacon list method
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
                    response.put("xA", String.valueOf(rs.getDouble("x")));
                    response.put("yA", String.valueOf(rs.getDouble("y")));
                } else if (i == 1) {
                    response.put("idB", rs.getString("beacon_id"));
                    response.put("xB", String.valueOf(rs.getDouble("x")));
                    response.put("yB", String.valueOf(rs.getDouble("y")));
                } else if (i == 2) {
                    response.put("idC", rs.getString("beacon_id"));
                    response.put("xC", String.valueOf(rs.getDouble("x")));
                    response.put("yC", String.valueOf(rs.getDouble("y")));
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
     * commonPlace method
     *
     * @param routingContext receives routing context from vertx.
     */
    static void commonPlace(final RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();

        /**
         * use hash map or a class object for response, and u can also use arraylist store some object.
         */

        List<Places> commonPlaceList = new ArrayList<>();


        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(url, userr, pass);
            Statement stmt = con.createStatement();



            String sql="select * from commonPlace";

            ResultSet rs=stmt.executeQuery(sql);
            while(rs.next()) {
                commonPlaceList.add(new Places(String.valueOf(rs.getDouble("x")), String.valueOf(rs.getDouble("x")), rs.getString("x")));
            }


            rs.close();
            stmt.close();
            con.close();
        } catch(Exception e) {
            e.printStackTrace();
        }


        returnResponse(routingContext, 200, commonPlaceList);
        return;
    }


    /**
     * getProductLoc by ASIN
     *
     * @param routingContext receives routing context from vertx.
     */
    static void getProductLoc(final RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();

        /**
         * use hash map or a class object for response, and u can also use arraylist store some object.
         */
        HashMap<String, String> response = new HashMap<>();

        String ASIN = request.getParam("ASIN");

        double x = -1;
        double y = -1;
        String price = "unknown";


        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(url, userr, pass);
            Statement stmt = con.createStatement();



            String sql="select * from products where ASIN = \'" + ASIN + "\'";

            System.out.println(sql);

            ResultSet rs=stmt.executeQuery(sql);
            while(rs.next()) {

                x = rs.getDouble("x");
                y = rs.getDouble("y");
                price = rs.getString("price");
                break;
            }


            rs.close();
            stmt.close();
            con.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        response.put("x", String.valueOf(x));

        response.put("y", String.valueOf(y));

        response.put("price", price);

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


    private static String URLHandler(String string) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String string2;
            String encoding="UTF-8";//utf8
            URL uRL = new URL(string);
            HttpURLConnection httpURLConnection = (HttpURLConnection) uRL
                    .openConnection();
            if (httpURLConnection.getResponseCode() != 200) {
                throw new IOException(httpURLConnection.getResponseMessage());
            }
            // use buffer to read the response
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream(), encoding));
            while ((string2 = bufferedReader.readLine()) != null) {
                stringBuilder.append(string2 + "\n");
            }
            bufferedReader.close();
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }



}
