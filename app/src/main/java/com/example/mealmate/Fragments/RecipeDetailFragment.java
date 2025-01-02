package com.example.mealmate.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mealmate.BuildConfig;
import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.example.mealmate.Utils.ShakeDetector;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeDetailFragment extends Fragment {

    TextView detailDesc, detailTitle, textViewIngredients, textStepsToCook;
    ImageView detailImage, addToMyRecipes, addToGroceryList, sendSms;
    FloatingActionButton deleteButton, editButton;
    String key, imageUrl, id;
    CheckBox checkboxPurchased;
    DatabaseReference databaseReference;
    private ShakeDetector shakeDetector;
    private Vibrator vibrator;

    // Gesture detector to handle swipe gestures
    private GestureDetector gestureDetector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // Initialize Views
        detailImage = view.findViewById(R.id.detailImage);
        detailTitle = view.findViewById(R.id.detailTitle);
        addToMyRecipes = view.findViewById(R.id.addToMyRecipes);
        addToGroceryList = view.findViewById(R.id.addToGroceryList);
        sendSms = view.findViewById(R.id.sendSms);
        textViewIngredients = view.findViewById(R.id.textViewIngredients);
        textStepsToCook = view.findViewById(R.id.textStepsToCook);
        deleteButton = view.findViewById(R.id.deleteButton);
        editButton = view.findViewById(R.id.editButton);
        checkboxPurchased = view.findViewById(R.id.checkboxPurchased);

        // Initialize services
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        shakeDetector = new ShakeDetector(requireContext(), this::deleteRecipe);

        // Initialize Gesture Detector
        gestureDetector = new GestureDetector(requireContext(), new GestureListener());

        // Retrieve arguments from the previous Fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            detailTitle.setText(bundle.getString("Title"));
            id = bundle.getString("id");
            key = bundle.getString("key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(imageUrl).into(detailImage);
            textViewIngredients.setText(bundle.getString("Ingredient"));

            // Fetch instructions using the API
            fetchRecipeInstructions(bundle.getString("id"));
        }

        // Setup event listeners
        deleteButton.setOnClickListener(view1 -> deleteRecipe());
        editButton.setOnClickListener(view12 -> navigateToEditRecipe());
        addToGroceryList.setOnClickListener(view13 -> addToGroceryList());
        sendSms.setOnClickListener(view14 -> sendRecipeViaSms());
        addToMyRecipes.setOnClickListener(view15 -> addToMyRecipes());

        // Handle checkbox state change
        checkboxPurchased.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                markRecipeAsPurchased();
            } else {
                unmarkRecipeAsPurchased();
            }
        });

        // Set touch listener to detect gestures
        view.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

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

    // GestureListener class to handle swipe gestures
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = event2.getY() - event1.getY();
                float diffX = event2.getX() - event1.getX();

                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX < 0) {
                        // Swipe Left - Trigger recipe deletion
                        deleteRecipe();
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    private void navigateToEditRecipe() {
        UpdateRecipeFragment updateRecipeFragment = new UpdateRecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Title", detailTitle.getText().toString());
        bundle.putString("Ingredient", textViewIngredients.getText().toString());
        bundle.putString("Image", imageUrl);
        bundle.putString("key", key);
        bundle.putString("id", id);
        bundle.putString("instructions", textStepsToCook.getText().toString());
        updateRecipeFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, updateRecipeFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToMyRecipes() {
        Fragment myRecipes = new MyRecipeFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, myRecipes)
                .addToBackStack(null)
                .commit();
    }

    private void addToGroceryList() {
        databaseReference = FirebaseDatabase.getInstance().getReference("mealmate_recipes_groceryList");
        String newKey = databaseReference.push().getKey();

        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setKey(newKey);
        recipe.setTitle(detailTitle.getText().toString());
        recipe.setImageURL(imageUrl);
        String ingredientsText = textViewIngredients.getText().toString();
        List<String> ingredientsList = Arrays.asList(ingredientsText.split(",\\s*"));
        recipe.setIngredients(ingredientsList);

        if (newKey != null) {
            databaseReference.child(newKey).setValue(recipe)
                    .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Recipe added to Grocery List!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to add recipe to Grocery List.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(requireContext(), "Failed to generate a unique key for the recipe.", Toast.LENGTH_SHORT).show();
        }
    }

    public void addToMyRecipes() {
        databaseReference = FirebaseDatabase.getInstance().getReference("mealmate_recipes");
        String newKey = databaseReference.push().getKey();

        Recipe recipe = new Recipe();
        recipe.setId(id);
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
        // Show confirmation dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // If the user confirms, proceed with the deletion
                    if (vibrator != null && vibrator.hasVibrator()) {
                        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                    }

                    if (key != null && !key.isEmpty()) {
                        databaseReference = FirebaseDatabase.getInstance()
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
                    } else {
                        Toast.makeText(getContext(), "Invalid recipe key", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // If the user cancels, just dismiss the dialog
                    dialog.dismiss();
                })
                .setCancelable(false)  // Prevent dismissal by tapping outside
                .show();
    }


    private void sendRecipeViaSms() {
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

    private void fetchRecipeInstructions(String recipeId) {
        String apiKey = BuildConfig.SPOONACULAR_API_KEY;
        String url = "https://api.spoonacular.com/recipes/" + recipeId + "/analyzedInstructions?apiKey=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray instructionsArray = new JSONArray(response);
                        if (instructionsArray.length() > 0) {
                            JSONArray stepsArray = instructionsArray.getJSONObject(0).getJSONArray("steps");
                            List<String> steps = new ArrayList<>();

                            for (int i = 0; i < stepsArray.length(); i++) {
                                JSONObject stepObject = stepsArray.getJSONObject(i);
                                String stepDescription = stepObject.getString("step");
                                steps.add(stepDescription);
                            }

                            String stepsHtml = generateNumberedList(steps);
                            textStepsToCook.setText(Html.fromHtml(stepsHtml, Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            textStepsToCook.setText("No instructions available.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing instructions", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Error fetching instructions: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }

    private String generateNumberedList(List<String> steps) {
        if (steps == null || steps.isEmpty()) {
            return "No steps provided.";
        }

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<ol>");
        for (String step : steps) {
            htmlBuilder.append("<li>").append(step).append("</li>");
        }
        htmlBuilder.append("</ol>");
        return htmlBuilder.toString();
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

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("mealmate_recipe_purchased")
                .child(key);

        databaseReference.setValue(purchasedRecipe)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Marked as purchased", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Failed to mark as purchased: " + e.getMessage(), e);
                    Toast.makeText(requireContext(), "Error marking as purchased", Toast.LENGTH_SHORT).show();
                });
    }

    private void unmarkRecipeAsPurchased() {
        if (key == null || key.isEmpty()) {
            Toast.makeText(getContext(), "Invalid recipe key", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("mealmate_recipe_purchased")
                .child(key);

        databaseReference.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Unmarked as purchased", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Failed to unmark as purchased: " + e.getMessage(), e);
                    Toast.makeText(requireContext(), "Error unmarking as purchased", Toast.LENGTH_SHORT).show();
                });
    }
}
