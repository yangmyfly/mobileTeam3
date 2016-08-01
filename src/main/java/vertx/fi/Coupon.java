package vertx.fi;

/**
 * Created by joe on 16/8/1.
 */
public class Coupon {

    public String coupon_id;
    public String user_id;
    public String description;


    public String expire_date;

    public Coupon(String coupon_id, String user_id, String description, String expire_date) {
        this.coupon_id = coupon_id;
        this.user_id = user_id;
        this.description = description;
        this.expire_date = expire_date;
    }
}
