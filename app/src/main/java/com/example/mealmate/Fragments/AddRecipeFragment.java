package com.example.mealmate.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddRecipeFragment extends Fragment {

    private ImageView uploadImage;
    private Button saveButton;
    private EditText uploadRecipe, uploadIngredients, uploadInstructions;
    private String imageURL;
    private Uri uri;

    private static final String FIREBASE_RECIPES_PATH = "mealmate_recipes";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        uploadImage = view.findViewById(R.id.uploadImage);
        uploadRecipe = view.findViewById(R.id.uploadRecipe);
        uploadIngredients = view.findViewById(R.id.uploadIngredients);
        uploadInstructions = view.findViewById(R.id.uploadInstructions);
        saveButton = view.findViewById(R.id.saveButton);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            uri = result.getData().getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(v -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        saveButton.setOnClickListener(v -> saveData());
        return view;
    }

    private void saveData() {
        if (uri == null) {
            Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference(FIREBASE_RECIPES_PATH + "/" + uri.getLastPathSegment());

        AlertDialog progressDialog = createProgressDialog();
        progressDialog.show();

        storageReference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnCompleteListener(uriTask -> {
                            if (uriTask.isSuccessful()) {
                                imageURL = uriTask.getResult().toString();
                                uploadData(progressDialog);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Failed to get image URL", Toast.LENGTH_SHORT).show();
                            }
                        }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadData(AlertDialog dialog) {
        String title = uploadRecipe.getText().toString().trim();
        String ingredientsInput = uploadIngredients.getText().toString().trim();
        String instructions = uploadInstructions.getText().toString().trim();

        if (title.isEmpty()) {
            dialog.dismiss();
            uploadRecipe.setError("Recipe title is required");
            uploadRecipe.requestFocus();
            return;
        }

        if (ingredientsInput.isEmpty()) {
            dialog.dismiss();
            uploadIngredients.setError("Ingredients are required");
            uploadIngredients.requestFocus();
            return;
        }

        List<String> ingredients = parseIngredients(ingredientsInput);
        String key = FirebaseDatabase.getInstance().getReference(FIREBASE_RECIPES_PATH).push().getKey();

        if (key != null) {
            Recipe recipe = new Recipe(title, imageURL, ingredients, instructions, null);
            FirebaseDatabase.getInstance().getReference(FIREBASE_RECIPES_PATH)
                    .child(key).setValue(recipe)
                    .addOnCompleteListener(task -> {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Recipe added successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    })
                    .addOnFailureListener(e -> {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            dialog.dismiss();
            Toast.makeText(getContext(), "Failed to generate recipe key", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> parseIngredients(String input) {
        Set<String> ingredientsSet = new HashSet<>();
        String[] ingredientArray = input.split(";");
        for (String item : ingredientArray) {
            if (!item.trim().isEmpty()) {
                ingredientsSet.add(item.trim());
            }
        }
        return new ArrayList<>(ingredientsSet);
    }

    private AlertDialog createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        return builder.create();
    }

    private void clearFields() {
        uploadRecipe.setText("");
        uploadIngredients.setText("");
        uploadInstructions.setText("");
        uploadImage.setImageResource(R.drawable.uploadimg);
        uri = null;
    }
}
