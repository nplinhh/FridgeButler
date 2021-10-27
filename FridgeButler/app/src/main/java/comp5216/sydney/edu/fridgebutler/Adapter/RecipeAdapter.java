package comp5216.sydney.edu.fridgebutler.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

import comp5216.sydney.edu.fridgebutler.R;
import comp5216.sydney.edu.fridgebutler.Recipe.Model.Recipe;

/**
 * Custom array adapter for listing recipe in RecipeRecommendation
 */
public class RecipeAdapter extends ArrayAdapter < Recipe > {

    //Constructor
    public RecipeAdapter(@NonNull Context context, List < Recipe > recipeList) {
        super(context, 0, recipeList);
    }

    //Populating individual recipe to View
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentItemView = convertView;

        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_adapter_view, parent, false);
        }

        Recipe recipePosition = getItem(position);

        //Load image to ImageView using Picasso
        String imageURL = recipePosition.getImage();
        Log.d(RecipeAdapter.class.getSimpleName(), imageURL);

        ImageView recipeImageView = currentItemView.findViewById(R.id.recipePic);
        assert recipePosition != null;

        Picasso.with(this.getContext()).load(imageURL).into(recipeImageView);

        TextView recipeName = currentItemView.findViewById(R.id.recipeName);
        recipeName.setText(recipePosition.getTitle());

        return currentItemView;
    }
}