package com.example.mealmate.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmate.BuildConfig;
import com.example.mealmate.Repository.RecipeSearchResponse;
import com.example.mealmate.R;
import com.example.mealmate.Repository.SpoonacularRecipeModel;
import com.example.mealmate.Repository.SpoonAcularAPI;
import com.example.mealmate.adapter.RecipeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomePageFragment extends Fragment {

    FloatingActionButton fab;
    private RecyclerView horizontalRecyclerView;
    private RecyclerView verticalRecyclerView;
    private RecipeAdapter horizontalAdapter;
    private final List<SpoonacularRecipeModel> recipes = new ArrayList<>();
    private final Handler handler = new Handler();
    private Runnable autoScroll;
    private static final String BASE_URL = "https://api.spoonacular.com/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Initialize views
        horizontalRecyclerView = view.findViewById(R.id.featuredRecyclerView);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        verticalRecyclerView = view.findViewById(R.id.recyclerView);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        // Initialize adapters
        horizontalAdapter = new RecipeAdapter(requireContext(), recipes);
        //verticalAdapter = new VerticalRecipeAdapter(requireContext(), recipes); // Adapter for vertical list

        horizontalRecyclerView.setAdapter(horizontalAdapter);
        verticalRecyclerView.setAdapter(horizontalAdapter);

        fab = view.findViewById(R.id.fab);

        // Set up progress bar
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE); // Show progress bar

        // Fetch recipes
        fetchRecipes(progressBar);

        // Set up auto-scroll for horizontal RecyclerView
        setupAutoScroll();

        // FAB click listener to open add recipe fragment
        fab.setOnClickListener(view1 -> {
            Fragment addRecipeFragment = new AddRecipeFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, addRecipeFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void fetchRecipes(ProgressBar progressBar) {
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpoonAcularAPI spoonacularApi = retrofit.create(SpoonAcularAPI.class);

        // API call
        Call<RecipeSearchResponse> call = spoonacularApi.fetchRandomRecipes(BuildConfig.SPOONACULAR_API_KEY, 20);
        call.enqueue(new Callback<RecipeSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecipeSearchResponse> call, @NonNull Response<RecipeSearchResponse> response) {
                Log.d("API_Response", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    RecipeSearchResponse recipeSearchResponse = response.body();
                    Log.d("API_Response", "Response Body: " + recipeSearchResponse);
                    if (recipeSearchResponse.getResults() != null && !recipeSearchResponse.getResults().isEmpty()) {
                        recipes.clear();
                        recipes.addAll(recipeSearchResponse.getResults());
                        horizontalAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("HomePageFragment", "No recipes found in response.");
                        Toast.makeText(requireContext(), "No recipes found. Try another category.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE); // Hide progress bar
                } else {
                    Log.e("HomePageFragment", "Failed to fetch recipes: " + response.code() + " " + response.message());
                    Toast.makeText(requireContext(), "Failed to load recipes. Try again!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeSearchResponse> call, @NonNull Throwable t) {
                Log.e("HomePageFragment", "Network error: " + t.getMessage());
                Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void setupAutoScroll() {
        autoScroll = () -> {
            LinearLayoutManager layoutManager = (LinearLayoutManager) horizontalRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                int nextItem = layoutManager.findFirstVisibleItemPosition() + 1;
                if (nextItem >= horizontalAdapter.getItemCount()) {
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
