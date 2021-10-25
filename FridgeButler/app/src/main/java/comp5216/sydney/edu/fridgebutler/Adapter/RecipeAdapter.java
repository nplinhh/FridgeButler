package comp5216.sydney.edu.fridgebutler.Adapter;

import android.content.Context;
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

public class RecipeAdatper extends ArrayAdapter<Recipe> {

    public RecipeAdatper(@NonNull Context context, List<Recipe> recipeList) {
        super(context,0, recipeList);
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_recipe_recommendation, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        Recipe recipePostion = getItem(position);

        // then according to the position of the view assign the desired image for the same
        String imageURL = recipePostion.getImage();
        ImageView recipeImageView = currentItemView.findViewById(R.id.recipePic);
        assert recipePostion != null;
//        recipeImageView.setImageResource();
        Picasso.with(this.getContext()).load(imageURL).into(recipeImageView);

        // then according to the position of the view assign the desired TextView 1 for the same
        TextView textView1 = currentItemView.findViewById(R.id.recipeTitle);
        textView1.setText(recipePostion.getTitle());

        // then according to the position of the view assign the desired TextView 2 for the same


        // then return the recyclable view
        return currentItemView;
    }
}
