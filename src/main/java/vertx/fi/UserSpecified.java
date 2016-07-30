package vertx.fi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class UserSpecified {
	private static String url = RouterClass.url;
	private static String userr = RouterClass.userr;
	private static String pass = RouterClass.pass;
	
    private static void returnResponse(final RoutingContext routingContext, int responseCode, Object response) {
        routingContext.response()
                .setStatusCode(responseCode)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(response));
    }
    
    // | shoppinglist_id | user_id | ASIN | title | imgurl
	static void shoppingLists (final RoutingContext routingContext) {

        HttpServerRequest request = routingContext.request();
        HashMap<String, String> response = new HashMap<>();
        
        String user_id = request.getParam("user_id");
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(url, userr, pass);
            Statement stmt = con.createStatement();

            String sql="select * from shopping_list where user_id = " + user_id;

            ResultSet rs=stmt.executeQuery(sql);
           
            int i = 0;
            while(rs.next()) {
            	StringBuilder sb = new StringBuilder();
            	sb.append(rs.getString("shoppingList_id"));
            	sb.append("::");
            	sb.append(rs.getString("ASIN"));
            	sb.append("::");
            	sb.append(rs.getString("title"));
            	sb.append("::");
            	sb.append(rs.getString("imgurl"));
            	
            	response.put(String.valueOf(i), sb.toString());
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
	
	static void editShoppingList (final RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HashMap<String, String> response = new HashMap<>();
        
        String user_id = request.getParam("user_id");
        String shoppinglist_id = request.getParam("shoppinglist_id");
        String op = request.getParam("op");
        
        if (op.equals("RM_LIST")) {
        	// delete the whole list
        } else if (op.equals("RM_ITEM")) {
        	// delete a product from the list
        } else if (op.equals("ADD_ITEM")) {
        	// add a product to a list.
        	// if the shoppinglist_id is a new one, it is adding a product to a new list
        }
	}
	
	// | coupon_id | user_id | description | expire_date |
	static void coupons (final RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HashMap<String, String> response = new HashMap<>();
        
        String user_id = request.getParam("user_id");
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(url, userr, pass);
            Statement stmt = con.createStatement();

            String sql="select * from coupons where user_id = " + user_id;

            ResultSet rs=stmt.executeQuery(sql);
           
            int i = 0;
            while(rs.next()) {
            	StringBuilder sb = new StringBuilder();
            	sb.append(rs.getString("coupon_id"));
            	sb.append("::");
            	sb.append(rs.getString("description"));
            	sb.append("::");
            	sb.append(rs.getString("expire_date"));
            	
            	response.put(String.valueOf(i), sb.toString());
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
	
	static void editCoupon (final RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HashMap<String, String> response = new HashMap<>();
        
        String user_id = request.getParam("user_id");
        String op = request.getParam("op");
        
        if (op.equals("add")) {
        	// add a coupon to the user
        } 
        else
        {
        	// delete the coupon
        }
	}
	
	// | user_id | nick_name | member_id |
	static void profile (final RoutingContext routingContext){
		HttpServerRequest request = routingContext.request();
        HashMap<String, String> response = new HashMap<>();
        
        String user_id = request.getParam("user_id");
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(url, userr, pass);
            Statement stmt = con.createStatement();

            String sql="select * from users where user_id = " + user_id;

            ResultSet rs=stmt.executeQuery(sql);
           
            response.put("nick_name", rs.getString("nick_name"));
            response.put("member_id", rs.getString("member_id"));

            rs.close();
            stmt.close();
            con.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        returnResponse(routingContext, 200, response);
        return;
	}
	
	static void editProfile (final RoutingContext routingContext){
		// overwrite the old profile with the new one
        HttpServerRequest request = routingContext.request();
        HashMap<String, String> response = new HashMap<>();
        
        String user_id = request.getParam("user_id");
        String nick_name = request.getParam("nick_name");
        String member_id = request.getParam("member_id");
        
        // insert to db
	}
	
	static void recommendations (final RoutingContext routingContext){
		// overwrite the old profile with the new one
        HttpServerRequest request = routingContext.request();
        HashMap<String, String> response = new HashMap<>();
        
        String user_id = request.getParam("user_id");
        String keyword = request.getParam("keyword");
        
        List<Products> list = MachineLearningRecommender.Predicting(user_id, keyword);
        // return back
	}
	
	static void history (final RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HashMap<String, String> response = new HashMap<>();
        
        String user_id = request.getParam("user_id");
        String ASIN = request.getParam("ASIN");
        
        // update model
        MachineLearningRecommender.Training(user_id, null);
	}
}