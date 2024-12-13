package com.example.mealmate.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.example.mealmate.adapter.RecipeAdapter;
import com.example.mealmate.BuildConfig;
import com.example.mealmate.adapter.RecipePagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class HomePageFragment extends Fragment {

    private RecyclerView horizontalRecyclerView;
    private FloatingActionButton fab;
    private RecipeAdapter recipeAdapter;
    private RecipePagerAdapter recipePagerAdapter;
    private final List<Recipe> recipes = new ArrayList<>();
    private final Handler handler = new Handler();
    private Runnable autoScroll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Initialize views
        horizontalRecyclerView = view.findViewById(R.id.featuredRecyclerView);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        RecyclerView verticalRecyclerView = view.findViewById(R.id.recyclerView);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        // Initialize adapters
        recipeAdapter = new RecipeAdapter(requireContext(), recipes);
        recipePagerAdapter = new RecipePagerAdapter(requireContext(), recipes);
        horizontalRecyclerView.setAdapter(recipeAdapter);
        verticalRecyclerView.setAdapter(recipePagerAdapter);

        // Initialize FAB
        fab = view.findViewById(R.id.fab);

        // Set up progress bar
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);  // Show loading indicator

        // Fetch random recipes
        fetchRandomRecipes(progressBar);

        // Set up auto-scroll for horizontal RecyclerView
        setupAutoScroll();

        // FAB click listener
        fab.setOnClickListener(view1 -> {
            Fragment addmyRecipe = getParentFragmentManager().findFragmentByTag("AddRecipeFragment");
            if (addmyRecipe == null) {
                addmyRecipe = new addRecipeFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, addmyRecipe, "AddRecipeFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void fetchRandomRecipes(ProgressBar progressBar) {
        String url = "https://api.spoonacular.com/recipes/random?number=20&apiKey=" + BuildConfig.SPOONACULAR_API_KEY;
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        recipes.clear(); // Clear the list before adding new recipes
                        JSONArray recipesArray = response.getJSONArray("recipes");

                        for (int i = 0; i < recipesArray.length(); i++) {

                            JSONObject recipe = recipesArray.getJSONObject(i);
                            String title = recipe.optString("title", "Untitled Recipe");
                            String imageUrl = recipe.optString("image", "");

                            JSONArray ingredientsArray = recipe.getJSONArray("extendedIngredients");

                            List<Recipe.InstructionIngredient> ingredients = new ArrayList<>();
                            for (int j = 0; j < ingredientsArray.length(); j++) {
                                JSONObject ingredient = ingredientsArray.getJSONObject(j);
                                int id = ingredient.getInt("id");
                                String name = ingredient.getString("name");
                                String image = ingredient.getString("image");

                                ingredients.add(new Recipe.InstructionIngredient(id, name, name, image));
                            }

                            recipes.add(new Recipe(null, title, imageUrl, null, null, ingredients, null, null));
                        }

                        recipeAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE); // Hide progress bar after data is loaded

                    } catch (JSONException e) {
                        Log.e("Recipe_error", "Error parsing JSON: " + e.getMessage());
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to fetch recipes", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE); // Hide progress bar on error
                });

        queue.add(jsonObjectRequest);
    }

    private void setupAutoScroll() {
        autoScroll = () -> {
            LinearLayoutManager layoutManager = (LinearLayoutManager) horizontalRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                int nextItem = layoutManager.findFirstVisibleItemPosition() + 1;
                if (nextItem >= recipeAdapter.getItemCount()) {
                    nextItem = 0;
                }
                horizontalRecyclerView.smoothScrollToPosition(nextItem);
                handler.postDelayed(autoScroll, 3000);
            }
        };
        handler.postDelayed(autoScroll, 3000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(autoScroll);
    }
}
