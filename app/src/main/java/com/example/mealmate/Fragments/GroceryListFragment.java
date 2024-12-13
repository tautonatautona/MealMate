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

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.example.mealmate.adapter.MyAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroceryListFragment extends Fragment {

    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<Recipe> dataList;
    MyAdapter adapter;
    SearchView searchView;
    ProgressBar progressBar;
    private final Handler searchHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.search);
        progressBar = view.findViewById(R.id.progressBar);

        searchView.clearFocus();

        // Set up GridLayoutManager with dynamic columns
        int numberOfColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Set up adapter
        dataList = new ArrayList<>();
        adapter = new MyAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);

        // Set up Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("mealmate_recipes_groceryList");

        // Show ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Firebase event listener to fetch data
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                updateDataList(snapshot);
                progressBar.setVisibility(View.GONE); // Hide ProgressBar after data is loaded
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE); // Hide ProgressBar on error
                Toast.makeText(getContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void updateDataList(DataSnapshot snapshot) {
        dataList.clear();
        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
            Recipe recipe = itemSnapshot.getValue(Recipe.class);
            if (recipe != null) {
                recipe.setKey(itemSnapshot.getKey());
                dataList.add(recipe);
            }
        }
        adapter.notifyDataSetChanged();
    }

    // Search for recipes by title
    public void searchList(String text) {
        ArrayList<Recipe> searchList = new ArrayList<>();
        for (Recipe recipe : dataList) {
            if (recipe.getDataTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(recipe);
            }
        }
        adapter.searchDataList(searchList);
    }
}
