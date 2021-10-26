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

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    public RecipeAdapter(@NonNull Context context, List<Recipe> recipeList) {
        super(context,0, recipeList);
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_adapter_view, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        Recipe recipePosition = getItem(position);

        // then according to the position of the view assign the desired image for the same
        String imageURL = recipePosition.getImage();
        Log.d(RecipeAdapter.class.getSimpleName(), imageURL);
        ImageView recipeImageView = currentItemView.findViewById(R.id.recipePic);
        assert recipePosition != null;
//        recipeImageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
        Picasso.with(this.getContext()).load(imageURL).into(recipeImageView);

        // then according to the position of the view assign the desired TextView 1 for the same
        TextView recipeName = currentItemView.findViewById(R.id.recipeName);
        recipeName.setText(recipePosition.getTitle());

        // then according to the position of the view assign the desired TextView 2 for the same


        // then return the recyclable view
        return currentItemView;
    }
}
