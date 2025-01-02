package com.example.mealmate.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.example.mealmate.BuildConfig;
import com.example.mealmate.Fragments.RecipeDetailFragment;
import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.example.mealmate.Repository.SpoonacularRecipeModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private final List<SpoonacularRecipeModel> recipes;
    private final Context context;

    public RecipeAdapter(Context context, List<SpoonacularRecipeModel> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        SpoonacularRecipeModel recipe = recipes.get(position);
        holder.titleTextView.setText(recipe.getTitle());
        Glide.with(context).load(recipe.getImageURL()).into(holder.imageView);

        // Navigate to RecipeDetailsFragment based on ID presence
        if (recipe.getId() == null || recipe.getId().isEmpty()) {
            // Firebase-stored recipes with no API calls
            holder.itemView.setOnClickListener(v -> navigateToManualRecipeDetails(recipe));
        } else {
            // Spoonacular API recipes
            holder.itemView.setOnClickListener(v -> navigateToRecipeDetails(recipe));
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    private void navigateToRecipeDetails(SpoonacularRecipeModel recipe) {
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        Bundle bundle = new Bundle();

        bundle.putString("Image", recipe.getImageURL());
        bundle.putString("Description", recipe.getDescription());
        bundle.putString("Title", recipe.getTitle());
        bundle.putString("Ingredient", String.valueOf(recipe.getIngredients()));
        bundle.putString("key", recipe.getKey());
        bundle.putString("id", recipe.getId());

        if (recipe instanceof Recipe) {
            Recipe castedRecipe = (Recipe) recipe;
            bundle.putString("Instructions", String.valueOf(castedRecipe.getInstructionsAsList()));
        } else {
            bundle.putString("Instructions", "No instructions available");
        }

        // Parse recipe ID and fetch details
        try {
            int id = Integer.parseInt(recipe.getId());
            fetchRecipeIngredients(id, bundle, recipeDetailFragment);
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Invalid recipe ID format.", Toast.LENGTH_SHORT).show();
            Log.e("RecipeAdapter", "Error parsing recipe ID: " + recipe.getId(), e);
        }
    }

    private void navigateToManualRecipeDetails(SpoonacularRecipeModel recipe) {
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key",recipe.getKey());
        bundle.putString("Image", recipe.getImageURL());
        bundle.putString("Description", recipe.getDescription());
        bundle.putString("Title", recipe.getTitle());
        bundle.putString("Ingredient", String.valueOf(recipe.getIngredients()));
        bundle.putString("Instructions", "No instructions available"); // Manual recipes lack instructions
        recipeDetailFragment.setArguments(bundle);

        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, recipeDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchRecipeIngredients(int recipeId, Bundle bundle, RecipeDetailFragment recipeDetailFragment) {
        String apiKey = BuildConfig.SPOONACULAR_API_KEY;
        String url = "https://api.spoonacular.com/recipes/" + recipeId + "/analyzedInstructions?apiKey=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("SpoonacularResponse", response);

                        try {
                            JSONArray recipesArray = new JSONArray(response);
                            StringBuilder ingredients = new StringBuilder();

                            for (int i = 0; i < recipesArray.length(); i++) {
                                JSONObject recipe = recipesArray.getJSONObject(i);
                                JSONArray stepsArray = recipe.getJSONArray("steps");

                                for (int j = 0; j < stepsArray.length(); j++) {
                                    JSONObject step = stepsArray.getJSONObject(j);

                                    if (step.has("ingredients") && step.getJSONArray("ingredients").length() > 0) {
                                        JSONArray ingredientsArray = step.getJSONArray("ingredients");

                                        for (int k = 0; k < ingredientsArray.length(); k++) {
                                            JSONObject ingredient = ingredientsArray.getJSONObject(k);
                                            String ingredientName = ingredient.getString("name");
                                            ingredients.append(ingredientName).append("\n");
                                        }
                                    }
                                }
                            }

                            if (ingredients.length() == 0) {
                                Toast.makeText(context, "No ingredients found in this recipe", Toast.LENGTH_SHORT).show();
                            }

                            bundle.putString("Ingredient", ingredients.toString());
                            recipeDetailFragment.setArguments(bundle);

                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, recipeDetailFragment)
                                    .addToBackStack(null)
                                    .commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error parsing ingredients", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error fetching ingredients", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(stringRequest);
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

    public void searchDataList(List<SpoonacularRecipeModel> filteredList) {
        this.recipes.clear();
        this.recipes.addAll(filteredList);
        notifyDataSetChanged();
    }
}
