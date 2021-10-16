package comp5216.sydney.edu.fridgebutler.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.function.Consumer;

import comp5216.sydney.edu.fridgebutler.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SelectedRecipe extends AppCompatActivity {
    TextView title;
    ListView IngredientsView, InstructionView;

    ArrayAdapter<String> ingredientsAdapter, instructionAdapter;
    ArrayList<String> ingredients = new ArrayList<String>();
    ArrayList<String> instructions = new ArrayList<String>();

    private MutableLiveData<Recipe> selectedRecipeLiveData = new MutableLiveData<>();
    private static final String TAG = SelectedRecipe.class.getSimpleName();
    private static final String BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/";
    Gson gson = new GsonBuilder().setLenient().create();
    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
    private final SpoonacularApi spoonacularApi =  retrofit.create(SpoonacularApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_recipe);

        title = findViewById(R.id.recipeTitle);
        IngredientsView = findViewById(R.id.ingredients);
        InstructionView = findViewById(R.id.instruction);

        ingredientsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingredients);
        instructionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, instructions);

        IngredientsView.setAdapter(ingredientsAdapter);
        InstructionView.setAdapter(instructionAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getString("recipeId");
            title.setText(id);
            fetchRecipeInformation(id, recipe -> selectedRecipeLiveData.setValue(recipe));
        }

    }

    public void fetchRecipeInformation(String recipeId, Consumer<Recipe> result) {
        Call<Recipe> recipeCall = spoonacularApi.getRecipeDetail(recipeId);
        recipeCall.enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                Recipe recipe = response.body();
                Log.d(TAG, "Response received: " + recipe.getId() + " " + recipe.getTitle());
                title.setText(recipe.getTitle());


                for (Ingredient item : recipe.getExtendedIngredients()) {
                    item.setImage("https://spoonacular.com/cdn/ingredients_100x100/" + item.getImage());
                    Log.d(TAG,"Check ingredients imported "+ item.getName());
                    ingredients.add(item.getAmount() +" " +item.getUnit() + " " + item.getName());
                    ingredientsAdapter.notifyDataSetChanged();
                }


                if (!recipe.getAnalyzedInstructions().isEmpty()) {
                    for (Instruction.Step step : recipe.getAnalyzedInstructions().get(0).getSteps()) {
                        Log.d(TAG, step.getNumber() + " " + step.getStep());
                        instructions.add(step.getNumber() +". " + step.getStep());
                        instructionAdapter.notifyDataSetChanged();
                    }
                }
                result.accept(recipe);
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                Log.e(TAG, "onFailure" + t.getMessage());
                result.accept(null);
            }
        });
    }
}