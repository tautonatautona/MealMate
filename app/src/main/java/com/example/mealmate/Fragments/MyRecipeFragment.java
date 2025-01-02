package com.example.mealmate.Fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.example.mealmate.Repository.SpoonacularRecipeModel;
import com.example.mealmate.adapter.RecipeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyRecipeFragment extends Fragment {

    FloatingActionButton fab;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<SpoonacularRecipeModel> dataList;
    RecipeAdapter adapter;
    SearchView searchView;
    ProgressBar progressBar;
    private final Handler searchHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_recipe, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        fab = view.findViewById(R.id.fab);
        searchView = view.findViewById(R.id.search);
        progressBar = view.findViewById(R.id.progressBar);

        searchView.clearFocus();

        // Set up GridLayoutManager with dynamic columns based on screen width
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columnWidth = getResources().getDimensionPixelSize(R.dimen.grid_column_width); // Define this in dimens.xml
        int spanCount = Math.max(1, screenWidth / columnWidth);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Set up adapter
        dataList = new ArrayList<>();
        adapter = new RecipeAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);

        // Set up Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("mealmate_recipes");

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

        // Search functionality with a delay
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchHandler.removeCallbacksAndMessages(null);
                searchHandler.postDelayed(() -> searchList(newText), 300);
                return true;
            }
        });

        // FAB click listener to open add recipe fragment
        fab.setOnClickListener(view1 -> {
            Fragment addRecipeFragment = new AddRecipeFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, addRecipeFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Attach ItemTouchHelper for swipe gestures with visual indication
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                SpoonacularRecipeModel recipe = dataList.get(position);

                // Show AlertDialog to confirm deletion
                showDeleteConfirmationDialog(recipe, position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                // Create a Paint object for the swipe background color
                Paint paint = new Paint();
                if (dX > 0) {
                    // Swipe to the right: set a green color
                    paint.setColor(Color.parseColor("#4CAF50"));
                } else if (dX < 0) {
                    // Swipe to the left: set a red color
                    paint.setColor(Color.parseColor("#F44336"));
                }

                // Draw the background color on the item being swiped
                c.drawRect(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(),
                        viewHolder.itemView.getRight(), viewHolder.itemView.getBottom(), paint);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
            Recipe recipe = itemSnapshot.getValue(Recipe.class);
            if (recipe != null) {
                recipe.setKey(itemSnapshot.getKey());
                dataList.add(recipe);
            }
        }
        adapter.notifyDataSetChanged();
    }

    // Firebase query for search functionality
    public void searchList(String text) {
        Query query = databaseReference.orderByChild("title").startAt(text).endAt(text + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                updateDataList(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to delete recipe from Firebase
    private void deleteRecipeFromFirebase(String recipeKey) {
        DatabaseReference recipeRef = databaseReference.child(recipeKey);
        recipeRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Successfully deleted
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete recipe", Toast.LENGTH_SHORT).show();
                });
    }

    // Show confirmation dialog before deleting the recipe
    private void showDeleteConfirmationDialog(SpoonacularRecipeModel recipe, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Proceed to delete the recipe if confirmed
                    deleteRecipeFromFirebase(recipe.getKey());
                    Toast.makeText(getContext(), "Recipe deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Restore the item in the adapter (revert swipe)
                    adapter.notifyItemChanged(position);
                })
                .setCancelable(false)  // Prevent dialog dismissal by tapping outside
                .show();
    }
}
