package com.example.mealmate.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private final List<Recipe> recipes;
    private final Context context;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.auto_scroll_recycler_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.titleTextView.setText(recipe.getDataTitle());
        Glide.with(context)
                .load(recipe.getDataImage())
                .into(holder.imageView);

        // Set up gesture detector for swipe right
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e2.getX() - e1.getX() > 100 && Math.abs(velocityX) > Math.abs(velocityY)) {
                    navigateToRecipeDetails(recipe);
                    return true;
                }
                return false;
            }
        });

        // Attach touch listener to the itemView
        holder.itemView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    private void navigateToRecipeDetails(Recipe recipe) {
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Image", recipe.getDataImage());
        bundle.putString("Description", recipe.getDataDesc());
        bundle.putString("Title", recipe.getDataTitle());
        bundle.putString("Key", recipe.getKey());
        bundle.putString("Ingredient", String.valueOf(recipe.getIngredients()));
        recipeDetailFragment.setArguments(bundle);

        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, recipeDetailFragment)
                .addToBackStack(null)
                .commit();

        Toast.makeText(context, "Swiped right: Viewing details", Toast.LENGTH_SHORT).show();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView imageView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.recTitle);
            imageView = itemView.findViewById(R.id.recImage);
        }
    }
}
