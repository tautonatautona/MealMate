package com.example.mealmate.Fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmate.R;
import com.example.mealmate.Repository.SpoonacularRecipeModel;  // Import RecipeBean
import com.example.mealmate.adapter.RecipeAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroceryListFragment extends Fragment {

    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private List<SpoonacularRecipeModel> dataList;  // Use RecipeBean
    private RecipeAdapter adapter;
    private ProgressBar progressBar;
    private TextView noResultsTextView;  // TextView to show "No Results"
    private final Handler searchHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);

        // Initialize views
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        SearchView searchView = view.findViewById(R.id.search);
        progressBar = view.findViewById(R.id.progressBar);
        noResultsTextView = view.findViewById(R.id.noResultsTextView);  // Initialize "No Results" TextView

        searchView.clearFocus();

        // Set up GridLayoutManager with dynamic columns
        int numberOfColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Initialize data list and adapter
        dataList = new ArrayList<>();
        adapter = new RecipeAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);

        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("mealmate_recipes_groceryList");

        // Show ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Firebase event listener to fetch data
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateDataList(snapshot);
                progressBar.setVisibility(View.GONE); // Hide ProgressBar after data is loaded
                noResultsTextView.setVisibility(dataList.isEmpty() ? View.VISIBLE : View.GONE);  // Show "No Results" if dataList is empty
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE); // Hide ProgressBar on error
                Toast.makeText(getContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                retryDataFetch();  // Retry after a delay
            }
        });

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchHandler.removeCallbacksAndMessages(null); // Remove pending search callbacks
                searchHandler.postDelayed(() -> searchList(newText), 300); // Delay search by 300ms
                return true;
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateDataList(DataSnapshot snapshot) {
        dataList.clear();
        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
            SpoonacularRecipeModel recipe = itemSnapshot.getValue(SpoonacularRecipeModel.class);  // Get RecipeBean
            if (recipe != null) {
                recipe.setKey(itemSnapshot.getKey());  // Set the Firebase key
                dataList.add(recipe);
            }
        }
        adapter.notifyDataSetChanged();
    }

    // Search for recipes by title or ingredients
    public void searchList(String text) {
        ArrayList<SpoonacularRecipeModel> searchList = new ArrayList<>();
        for (SpoonacularRecipeModel recipe : dataList) {
            if (recipe.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    recipe.getIngredients().contains(text.toLowerCase())) {  // Check ingredients as well
                searchList.add(recipe);
            }
        }
        adapter.searchDataList(searchList);  // Update adapter with search results
        noResultsTextView.setVisibility(searchList.isEmpty() ? View.VISIBLE : View.GONE);  // Show "No Results" if search returns empty
    }

    // Retry fetching data from Firebase
    private void retryDataFetch() {
        Toast.makeText(getContext(), "Retrying...", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.VISIBLE);
            databaseReference.addValueEventListener(eventListener);  // Retry fetching data
        }, 3000);  // Retry after 3 seconds
    }
}
