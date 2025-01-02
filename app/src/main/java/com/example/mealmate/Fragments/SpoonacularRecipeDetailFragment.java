package com.example.mealmate.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.example.mealmate.Utils.ShakeDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpoonacularRecipeDetailFragment extends Fragment {

    TextView detailTitle, textViewIngredients, textStepsToCook;
    ImageView detailImage, sendSms, addToMyRecipes;
    CheckBox checkboxPurchased;
    String key, imageUrl;
    DatabaseReference databaseReference;
    private ShakeDetector shakeDetector;
    private android.os.Vibrator vibrator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spoonacula_recipe_detail, container, false);

        // Initialize Views
        detailImage = view.findViewById(R.id.detailImage);
        detailTitle = view.findViewById(R.id.detailTitle);
        sendSms = view.findViewById(R.id.sendSms);
        textViewIngredients = view.findViewById(R.id.textViewIngredients);
        textStepsToCook = view.findViewById(R.id.textStepsToCook);
        addToMyRecipes = view.findViewById(R.id.addToMyRecipes);
        checkboxPurchased = view.findViewById(R.id.checkboxPurchased);

        // Initialize services
        vibrator = (android.os.Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        shakeDetector = new ShakeDetector(requireContext(), this::deleteRecipe);

        // Retrieve arguments from the previous Fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            detailTitle.setText(bundle.getString("Title"));
            key = bundle.getString("key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(imageUrl).into(detailImage);
            textViewIngredients.setText(bundle.getString("Ingredient"));

            // Handle numbered steps for instructions
            List<String> recipeSteps = Collections.singletonList(bundle.getString("Instructions"));
            String stepsHtml = generateNumberedList(recipeSteps);
            textStepsToCook.setText(Html.fromHtml(stepsHtml, Html.FROM_HTML_MODE_LEGACY));
        }

        // Setup event listeners
        sendSms.setOnClickListener(view1 -> sendRecipeViaSms());
        addToMyRecipes.setOnClickListener(view1 -> addToMyRecipes());

        // Handle checkbox state change
        checkboxPurchased.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                markRecipeAsPurchased();
            } else {
                unmarkRecipeAsPurchased();
            }
        });

        return view;
    }

    private void markRecipeAsPurchased() {
        if (key == null || key.isEmpty()) {
            Toast.makeText(getContext(), "Invalid recipe key", Toast.LENGTH_SHORT).show();
            return;
        }

        Recipe purchasedRecipe = new Recipe();
        purchasedRecipe.setId(key);
        purchasedRecipe.setTitle(detailTitle.getText().toString().trim());
        purchasedRecipe.setImageURL(imageUrl);

        // Add to "mealmate_recipe_purchased" node
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("mealmate_recipe_purchased")
                .child(key);

        databaseReference.setValue(purchasedRecipe)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Marked as purchased", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Failed to mark as purchased: " + e.getMessage(), e);
                    Toast.makeText(requireContext(), "Failed to mark as purchased.", Toast.LENGTH_SHORT).show();
                });
    }

    private void unmarkRecipeAsPurchased() {
        if (key == null || key.isEmpty()) {
            Toast.makeText(getContext(), "Invalid recipe key", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove from "mealmate_recipe_purchased" node
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("mealmate_recipe_purchased")
                .child(key);

        databaseReference.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Unmarked as purchased", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Failed to unmark as purchased: " + e.getMessage(), e);
                    Toast.makeText(requireContext(), "Failed to unmark as purchased.", Toast.LENGTH_SHORT).show();
                });
    }

    private String generateNumberedList(List<String> items) {
        StringBuilder numberedList = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            numberedList.append("<b>").append(i + 1).append(".</b> ").append(items.get(i)).append("<br>");
        }
        return numberedList.toString();
    }

    private void sendRecipeViaSms() {
        // Example SMS sending logic (you can customize this part as needed)
        String message = "Check out this recipe: " + detailTitle.getText().toString() + "\n" +
                "Ingredients: " + textViewIngredients.getText().toString();

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra("sms_body", message);
            intent.setType("vnd.android-dir/mms-sms");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addToGroceryList() {

        databaseReference = FirebaseDatabase.getInstance().getReference("mealmate_recipes_groceryList");
        String newKey = databaseReference.push().getKey();

        Recipe recipe = new Recipe();
      //  recipe.setId(id);
        recipe.setKey(newKey);
        recipe.setTitle(detailTitle.getText().toString());
        recipe.setImageURL(imageUrl);
        String ingredientsText = textViewIngredients.getText().toString();
// Split the ingredients string into a list, assuming they are comma-separated
        List<String> ingredientsList = Arrays.asList(ingredientsText.split(",\\s*"));

// Set the ingredients to the recipe
        recipe.setIngredients(ingredientsList);


        if (newKey != null) {
            databaseReference.child(newKey).setValue(recipe)
                    .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Recipe added to Grocery List!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to add recipe to Grocery List.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(requireContext(), "Failed to generate a unique key for the recipe.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToMyRecipes() {

        databaseReference = FirebaseDatabase.getInstance().getReference("mealmate_recipes");
        String newKey = databaseReference.push().getKey();

        Recipe recipe = new Recipe();
       // recipe.setId(id);
        recipe.setKey(newKey);
        recipe.setTitle(detailTitle.getText().toString());
        recipe.setImageURL(imageUrl);

        if (newKey != null) {
            databaseReference.child(newKey).setValue(recipe)
                    .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Recipe added to my recipes!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to add recipe to my recipes.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(requireContext(), "Failed to generate a unique key for the recipe.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRecipe() {
        // Implement recipe deletion logic here
        Toast.makeText(requireContext(), "Shake detected! Recipe deleted.", Toast.LENGTH_SHORT).show();
    }
}
