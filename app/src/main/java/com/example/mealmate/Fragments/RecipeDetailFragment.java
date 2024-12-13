package com.example.mealmate.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.example.mealmate.Utils.ShakeDetector;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.bumptech.glide.Glide;
import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Objects;

public class RecipeDetailFragment extends Fragment {

    TextView detailDesc, detailTitle, textViewIngredients, textStepsToCook;
    ImageView detailImage, addToGroceryList, sendSms;
    FloatingActionButton deleteButton, editButton;
    String key;
    String imageUrl;
    DatabaseReference databaseReference;
    Recipe recipe;

    private ShakeDetector shakeDetector;
    private Vibrator vibrator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        detailDesc = view.findViewById(R.id.detailDesc);
        detailImage = view.findViewById(R.id.detailImage);
        detailTitle = view.findViewById(R.id.detailTitle);
        addToGroceryList = view.findViewById(R.id.addToGroceryList);
        sendSms = view.findViewById(R.id.sendSms);
        textViewIngredients = view.findViewById(R.id.textViewIngredients);
        textStepsToCook = view.findViewById(R.id.textStepsToCook);
        deleteButton = view.findViewById(R.id.deleteButton);
        editButton = view.findViewById(R.id.editButton);

        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        shakeDetector = new ShakeDetector(requireContext(), this::deleteRecipe);

        Bundle bundle = getArguments();

        if (bundle != null) {
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(imageUrl).into(detailImage);
            textViewIngredients.setText(bundle.getString("Ingredient"));
            textStepsToCook.setText("Steps to Cook: ");
        }

        deleteButton.setOnClickListener(view1 -> deleteRecipe());
        editButton.setOnClickListener(view12 -> navigateToEditRecipe());
        addToGroceryList.setOnClickListener(view13 -> addToGroceryList());
        sendSms.setOnClickListener(view14 -> sendRecipeViaSms());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        shakeDetector.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        shakeDetector.stop();
    }

    private void deleteRecipe() {
        if (key == null || key.isEmpty()) {
            Toast.makeText(getContext(), "Invalid item key", Toast.LENGTH_SHORT).show();
            return;
        }

        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        }


        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("mealmate_recipes")
                .child(key);

        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Item deleted successfully", Toast.LENGTH_SHORT).show();
                navigateToMyRecipes();
            } else {
                Toast.makeText(getContext(), "Failed to delete the item", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void navigateToEditRecipe() {
        UpdateRecipeFragment updateRecipeFragment = new UpdateRecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Title", detailTitle.getText().toString());
        bundle.putString("Description", detailDesc.getText().toString());
        bundle.putString("Image", imageUrl);
        bundle.putString("Key", key);
        updateRecipeFragment.setArguments(bundle);

        Objects.requireNonNull(requireActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, updateRecipeFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToMyRecipes() {
        Fragment myRecipes = new MyRecipeFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, myRecipes)
                .addToBackStack(null)
                .commit();
    }

    private void addToGroceryList() {
        recipe = new Recipe();
        recipe.setDataTitle(detailTitle.getText().toString());
        recipe.setDataImage(imageUrl);
        recipe.setKey(key);

        databaseReference = FirebaseDatabase.getInstance().getReference("mealmate_recipes_groceryList");
        String newKey = databaseReference.push().getKey();
        if (newKey != null) {
            databaseReference.child(newKey).setValue(recipe)
                    .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Recipe added to Grocery List!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to add recipe to Grocery List.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(requireContext(), "Failed to generate a unique key for the recipe.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRecipeViaSms() {
        try {
            String phoneNumber = "1234567890"; // Replace with desired phone number
            String message = "Recipe: " + detailTitle.getText().toString() + "\n" +
                    "Description: " + detailDesc.getText().toString() + "\n" +
                    "Ingredients: " + textViewIngredients.getText().toString();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra("address", phoneNumber);
            intent.putExtra("sms_body", message);
            intent.setType("vnd.android-dir/mms-sms");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
