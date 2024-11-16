package com.example.mealmate.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomePageFragment extends Fragment {

    private RecyclerView horizontalRecyclerView;
    private RecipeAdapter recipeAdapter;
    private final List<Recipe> recipes = new ArrayList<>();
    private final Handler handler = new Handler();
    private Runnable autoScroll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Initialize RecyclerViews
        horizontalRecyclerView = view.findViewById(R.id.featuredRecyclerView);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        RecyclerView verticalRecyclerView = view.findViewById(R.id.recyclerView);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        // Initialize adapter
        recipeAdapter = new RecipeAdapter(requireContext(), recipes);
        horizontalRecyclerView.setAdapter(recipeAdapter);
        verticalRecyclerView.setAdapter(recipeAdapter);

        // Fetch random recipes and start auto-scroll
        fetchRandomRecipes();
        setupAutoScroll();

        return view;
    }

    private void fetchRandomRecipes() {
        String url = "https://api.spoonacular.com/recipes/random?number=20&apiKey=" + BuildConfig.SPOONACULAR_API_KEY;
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        recipes.clear();  // Clear list before adding new items
                        JSONArray recipesArray = response.getJSONArray("recipes");

                        for (int i = 0; i < recipesArray.length(); i++) {
                            JSONObject recipe = recipesArray.getJSONObject(i);
                            String title = recipe.getString("title");
                            String imageUrl = recipe.getString("image");

                            recipes.add(new Recipe(title, imageUrl));
                        }
                        recipeAdapter.notifyDataSetChanged();
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
