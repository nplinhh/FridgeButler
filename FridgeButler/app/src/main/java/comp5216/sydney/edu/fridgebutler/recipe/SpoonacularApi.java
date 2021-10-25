package comp5216.sydney.edu.fridgebutler.recipe;

import java.util.List;

import comp5216.sydney.edu.fridgebutler.adapter.Item;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularApi {
    static final String API_KEY_HEADER = "X-RapidAPI-Key: bb3f801f97mshb27f34a96f064c1p11f15ajsn1931856cc647";

    @GET("recipes/findByIngredients?number=5")
    @Headers(API_KEY_HEADER)
    Call<List<Recipe>> getRecipesID(@Query("ingredients") String ingredients);

    @GET("recipes/{id}/information")
    @Headers(API_KEY_HEADER)
    Call<Recipe> getRecipeDetail(@Path("id") String recipeId);




}
