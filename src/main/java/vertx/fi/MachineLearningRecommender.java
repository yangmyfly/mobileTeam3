package vertx.fi;

import java.util.*;

public class MachineLearningRecommender {
	// when the user browsed the "newProduct", the model of this user is updated accordingly
	public static void Training (String userId, Products newProduct)
	{
		
	}
	
	// when the user is creating a shopping list, we predict (recommend) a product based on the trained model
	// when the user is in the home screen, we recommend products without knowing what the user is searching for
	public static List<Products> Predicting(String userId, String keyword)
	{
		return null;
	}
}
