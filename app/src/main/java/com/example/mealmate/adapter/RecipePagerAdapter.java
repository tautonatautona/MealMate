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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mealmate.BuildConfig;
import com.example.mealmate.Fragments.SpoonacularRecipeDetailFragment;
import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.example.mealmate.Repository.SpoonacularRecipeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipePagerAdapter extends RecyclerView.Adapter<RecipePagerAdapter.ViewHolder> {

    private final Context context;
    private List<SpoonacularRecipeModel> recipes;

    public RecipePagerAdapter(Context context, List<SpoonacularRecipeModel> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpoonacularRecipeModel recipe = recipes.get(position);

        if (recipe != null && recipe.getTitle() != null && !recipe.getTitle().isEmpty()) {
            holder.textView.setText(recipe.getTitle());

            Glide.with(holder.itemView.getContext())
                    .load(recipe.getImageURL())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .override(400, 400)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(v -> {
                SpoonacularRecipeDetailFragment spoonacularRecipeDetailFragment = navigateToRecipeDetails(recipe);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, spoonacularRecipeDetailFragment)
                        .addToBackStack(null)
                        .commit();
            });
        } else {
            Toast.makeText(context, "Invalid recipe data", Toast.LENGTH_SHORT).show();
        }
    }

    private SpoonacularRecipeDetailFragment navigateToRecipeDetails(SpoonacularRecipeModel recipe) {
        SpoonacularRecipeDetailFragment spoonacularRecipeDetailFragment = new SpoonacularRecipeDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Image", recipe.getImageURL());
        bundle.putString("Description", recipe.getDescription());
        bundle.putString("Title", recipe.getTitle());
        bundle.putString("Key", String.valueOf(recipe.getId()));
        bundle.putString("Ingredient", String.valueOf(recipe.getIngredients()));

        if (recipe instanceof Recipe) {
            // Cast recipe to Recipe and call getInstructionsAsList()
            Recipe castedRecipe = (Recipe) recipe;
            bundle.putString("Instructions", String.valueOf(castedRecipe.getInstructionsAsList()));
        } else {
            // Handle the case where recipe is not of type Recipe
            bundle.putString("Instructions", "No instructions available");
        }
        // Make an API call to fetch the ingredients
        fetchRecipeIngredients(Integer.parseInt(recipe.getId()), bundle, spoonacularRecipeDetailFragment);
        return spoonacularRecipeDetailFragment;
    }

    private void fetchRecipeIngredients(int recipeId, Bundle bundle, SpoonacularRecipeDetailFragment recipeDetailFragment) {
        String apiKey = BuildConfig.SPOONACULAR_API_KEY;
        String url = "https://api.spoonacular.com/recipes/" + recipeId + "/analyzedInstructions?apiKey=" + apiKey;

        // Create a new request queue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Create the StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log the response to see its structure
                        Log.d("SpoonacularResponse", response);

                        // Parse the response and get ingredients
                        try {
                            JSONArray recipesArray = new JSONArray(response);

                            // StringBuilder to store ingredients
                            StringBuilder ingredients = new StringBuilder();

                            for (int i = 0; i < recipesArray.length(); i++) {
                                JSONObject recipe = recipesArray.getJSONObject(i);
                                JSONArray stepsArray = recipe.getJSONArray("steps");

                                for (int j = 0; j < stepsArray.length(); j++) {
                                    JSONObject step = stepsArray.getJSONObject(j);

                                    // Only process steps with ingredients
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

                            // If no ingredients were found, display a message
                            if (ingredients.length() == 0) {
                                Toast.makeText(context, "No ingredients found in this recipe", Toast.LENGTH_SHORT).show();
                            }

                            // Add the ingredients to the bundle
                            bundle.putString("Ingredient", ingredients.toString());

                            // Set the bundle in the fragment
                            recipeDetailFragment.setArguments(bundle);

                            // Navigate to the RecipeDetailFragment
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

        // Add the request to the queue
        queue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    // Method to update the data list with search results
    public void searchDataList(ArrayList<SpoonacularRecipeModel> searchList) {
        this.recipes = new ArrayList<>(searchList);
        notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
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
