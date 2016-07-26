package vertx.fi;

/**
 * Created by joe on 16/7/26.
 */
public class Products {
    public double x;
    public double y;
    public String title;
    public String ASIN;


    public String image;
    public String description;
    public String customReview;

    public Products(double x, double y, String title, String ASIN,
                    String image, String description, String customReview) {
        this.x = x;
        this.y = y;
        this.title = title;
        this.ASIN = ASIN;
        this.image = image;
        this.description = description;
        this.customReview = customReview;
    }

}
