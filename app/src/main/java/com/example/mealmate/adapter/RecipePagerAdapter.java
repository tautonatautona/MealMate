package com.example.mealmate.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.Fragments.RecipeDetailFragment;
import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RecipePagerAdapter extends RecyclerView.Adapter<RecipePagerAdapter.ViewHolder> {

    private final List<Recipe> recipes;
    private final Context context;
    private final DatabaseReference databaseReference;

    // Constructor for RecipeAdapter
    public RecipePagerAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
        // Initialize Firebase Realtime Database reference
        this.databaseReference = FirebaseDatabase.getInstance().getReference("mealmate_recipes");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        // Validate the recipe data before proceeding
        if (recipe != null && recipe.getDataTitle() != null && !recipe.getDataTitle().isEmpty()) {
            holder.textView.setText(recipe.getDataTitle());

            Glide.with(holder.itemView.getContext())
                    .load(recipe.getDataImage()) // Your image URL here
                    .placeholder(R.drawable.placeholder_image) // Show this while loading
                    .error(R.drawable.error_image) // Show this if there is an error
                    .override(400, 400) // Adjust the size for optimization
                    .into(holder.imageView);

            // Set click listener to navigate to RecipeDetailFragment
            holder.itemView.setOnClickListener(v -> {

                RecipeDetailFragment recipeDetailFragment = getRecipeDetailFragment(recipe);

                // Replace the current fragment and add it to the back stack
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, recipeDetailFragment, "RecipeDetailFragment")
                        .addToBackStack(null) // Optional: adds to the back stack
                        .commit();
            });

        } else {
            // Show a Toast if the recipe data is invalid
            Toast.makeText(context, "Invalid recipe data", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private static RecipeDetailFragment getRecipeDetailFragment(Recipe recipe) {
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Image", recipe.getDataImage());
        bundle.putString("Description", recipe.getDataDesc());
        bundle.putString("Title", recipe.getDataTitle());
        bundle.putString("Key", recipe.getKey());
        bundle.putString("Ingredient", recipe.getAnalyzedInstructions());
        recipeDetailFragment.setArguments(bundle);
        return recipeDetailFragment;
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recImage);
            textView = itemView.findViewById(R.id.recTitle);
        }
    }
}
