package com.example.mealmate.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class addRecipeFragment extends Fragment {

    ImageView uploadImage;
    Button saveButton;
    EditText uploadRecipe, uploadDescription,uploadIngredients;
    String imageURL;
    Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        // Initialize UI components
        uploadImage = view.findViewById(R.id.uploadImage);
        uploadDescription = view.findViewById(R.id.uploadDesc);
        uploadRecipe = view.findViewById(R.id.uploadRecipe);
        uploadIngredients = view.findViewById(R.id.uploadIngredients);
        saveButton = view.findViewById(R.id.saveButton);

        // Set up ActivityResultLauncher for image selection
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            assert data != null;
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Image upload click event
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        // Save button click event
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        return view;
    }

    // Save data to Firebase Storage and Database
    public void saveData() {
        if (uri != null) {
            // Reference to Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference("mealmate_recipes/" + uri.getLastPathSegment());

            // Create and show progress dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            // Upload image to Firebase Storage
            storageReference.putFile(uri).addOnSuccessListener(taskSnapshot ->
                    // Get the download URL
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(uriTask -> {
                        if (uriTask.isSuccessful()) {
                            Uri urlImage = uriTask.getResult();
                            if (urlImage != null) {
                                imageURL = urlImage.toString();
                                uploadData(); // Call method to upload data to Firebase Database
                            }
                            dialog.dismiss(); // Dismiss progress dialog
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Failed to get image URL: " + uriTask.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
            ).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadData() {
        String title = uploadRecipe.getText().toString().trim();
        String desc = uploadDescription.getText().toString().trim();
        String ingredientsInput = uploadIngredients.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || ingredientsInput.isEmpty()) {
            Toast.makeText(getActivity(), "Please provide all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse ingredients
        List<Recipe.InstructionIngredient> ingredients = new ArrayList<>();
        String[] ingredientArray = ingredientsInput.split(";"); // Assuming ';'-separated ingredients
        for (String item : ingredientArray) {
            String[] details = item.split(","); // Assuming ',' separates fields
            if (details.length >= 4) {
                int id = ingredients.size() + 1; // Example logic for unique ID
                String name = details[0].trim();
                String localizedName = details[1].trim();
                String image = details[2].trim();

                Recipe.InstructionIngredient ingredient = new Recipe.InstructionIngredient(id, name, localizedName, image);
                ingredients.add(ingredient);
            }
        }

        // Create RecipeInstruction
        Recipe.RecipeInstruction instruction = new Recipe.RecipeInstruction(1, "Example step", ingredients, new ArrayList<>(), null);
        List<Recipe.RecipeInstruction> instructions = new ArrayList<>();
        instructions.add(instruction);

        // Create Recipe object
        String key = FirebaseDatabase.getInstance().getReference("mealmate_recipes").push().getKey();
        if (key != null) {
            Recipe recipe = new Recipe(null,title, "", desc, key, ingredients,null, null);

            FirebaseDatabase.getInstance().getReference("mealmate_recipes").child(key).setValue(recipe)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Recipe added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to add recipe.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getActivity(), "Failed to generate a recipe key.", Toast.LENGTH_SHORT).show();
        }


        if (!title.isEmpty() && !desc.isEmpty() && !ingredients.isEmpty() && imageURL != null) {
            Recipe recipe = new Recipe(null, title, imageURL, desc, null, ingredients,null, null);
            String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

            FirebaseDatabase.getInstance().getReference("mealmate_recipes").child(currentDate)
                    .setValue(recipe)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Recipe added successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to add recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getActivity(), "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show();
        }


    // Validate inputs
        if (!title.isEmpty() && !desc.isEmpty() && !ingredients.isEmpty() && imageURL != null) {
            Recipe recipe = new Recipe(null, title, imageURL, desc, null, ingredients, ingredientsInput,null);
            String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

            // Upload to Firebase
            FirebaseDatabase.getInstance().getReference("mealmate_recipes").child(currentDate)
                    .setValue(recipe)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Recipe added successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to add recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getActivity(), "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show();
        }
    }

}
