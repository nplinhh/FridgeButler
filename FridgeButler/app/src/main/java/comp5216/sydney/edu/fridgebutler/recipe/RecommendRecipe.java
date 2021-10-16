package comp5216.sydney.edu.fridgebutler.recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import comp5216.sydney.edu.fridgebutler.R;
import comp5216.sydney.edu.fridgebutler.adapter.DataCallBack;
import comp5216.sydney.edu.fridgebutler.adapter.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RecommendRecipe extends AppCompatActivity {
    ListView listView;
    ArrayList<String> recipeList;
    ArrayAdapter<String> itemsAdapter;

    MutableLiveData<List<Recipe>> recipeLiveData = new MutableLiveData<>();


    private static final String TAG = SpoonacularApi.class.getSimpleName();
    private static final String BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/";
    Gson gson = new GsonBuilder().setLenient().create();
    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
    private final SpoonacularApi spoonacularApi =  retrofit.create(SpoonacularApi.class);

    public FirebaseAuth firebaseAuth;
    public FirebaseFirestore db;
    public String user_id;
    public ArrayList <Item> ingredientsList = new ArrayList<Item>();
    public ArrayList <String> ingredientsListString = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_recipe);

        //Set XML
        listView = (ListView) findViewById(R.id.recipeList);

        recipeList = new ArrayList<String>();

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipeList);
        listView.setAdapter(itemsAdapter);

        //set database instance
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        loadData(new DataCallBack() {
            @Override
            public void onComplete(ArrayList<Item> item) {
                Log.d(TAG, "Check ingredients list size" + String.valueOf(ingredientsList.size()));
                for(int i = 0; i < ingredientsList.size(); i++ ){
                    ingredientsListString.add(ingredientsList.get(i).getName());
                }
                Log.d(TAG, "Check ingredients list string size" + String.valueOf(ingredientsListString.size()));
                fetchRecipesByIngredient(ingredientsListString, recipes -> recipeLiveData.postValue(recipes));


            }

        });


//        List<String> ingredients = new ArrayList<String>();
//        ingredients.add("chicken");
//        ingredients.add("beef");
//        fetchRecipesByIngredient(ingredients, recipes -> recipeLiveData.postValue(recipes));

//        SpoonacularRepository spoonacularRepository = new SpoonacularRepository();
//        spoonacularRepository.fetchRecipesByIngredient(ingredients, recipes -> recipeLiveData.postValue(recipes));
//        spoonacularRepository.fetchRecipeInformation(recipeId, recipe -> selectedRecipeLiveData.setValue(recipe));

    }

    public void fetchRecipesByIngredient(List<String> ingredients, Consumer<List<Recipe>> result) {
        Call<List<Recipe>> recipesCall = null;
        ArrayList<String> RecipeIdList = new ArrayList<String>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            recipesCall = spoonacularApi.getRecipesID(String.join(",", ingredients));
        }
        recipesCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                Log.d(TAG, "New Response received: " + response.body().size());

                for (Recipe recipe : response.body()) {
                    Log.d(TAG,  recipe.getId() + " " + recipe.getTitle());
                    recipeList.add(recipe.getTitle());
                    RecipeIdList.add(recipe.getId());
                    itemsAdapter.notifyDataSetChanged();
                }
                result.accept(response.body());
                setupListViewListener(RecipeIdList);
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "onFailure" + t.getMessage());
                result.accept(Collections.emptyList());
            }
        });
    }

    public void loadData(DataCallBack dataCallBack){
        CollectionReference collectionRef = db.collection("ingredients").document(user_id).collection("IngredientList");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item item= new Item(document.get("Name").toString(), document.get("expiryDate").toString(), document.getId());
                        ingredientsList.add(item);
                        Log.d(TAG, "Sucessfully retrieved: "+ document.get("Name").toString());
                    }
                    dataCallBack.onComplete(ingredientsList);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void setupListViewListener(ArrayList<String> RecipeIdList) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowID) {
                Intent launchRecipe  = new Intent(RecommendRecipe.this,SelectedRecipe.class);
                launchRecipe.putExtra("recipeId" , RecipeIdList.get(position));
                startActivity(launchRecipe);
            }
        });

    }



//    interface SpoonacularApi {
//        @GET("recipes/findByIngredients?number=5")
//        @Headers(API_KEY_HEADER)
//        Call<List<Recipe>> getRecipesByIngredient(@Query("ingredients") String ingredients);
//
//        @GET("recipes/{id}/information")
//        @Headers(API_KEY_HEADER)
//        Call<Recipe> getRecipeInformation(@Path("id") String recipeId);
//
//        @GET("recipes/{id}/similar")
//        @Headers(API_KEY_HEADER)
//        Call<List<Recipe>> getRelatedRecipesForRecipe(@Path("id") String recipeId);
//
//        @GET("food/ingredients/autocomplete?number=1")
//        @Headers(API_KEY_HEADER)
//        Call<List<Item>> getFoodInformation(@Query("query") String foodName);
//
//    }

}