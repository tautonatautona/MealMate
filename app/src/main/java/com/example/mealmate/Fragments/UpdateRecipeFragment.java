package com.example.mealmate.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UpdateRecipeFragment extends Fragment {

    private static final String TAG = "UpdateRecipeFragment";

    private ImageView updateImage;
    private Button updateButton;
    private EditText updateInstructions, updateTitle, updateIngredients;
    private String title, ingredients, instructions;
    private String imageUrl;
    private String key, oldImageURL;
    private Uri uri;
    private DatabaseReference databaseReference;
    private AlertDialog dialog;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_recipe, container, false);

        updateButton = view.findViewById(R.id.updateButton);
        updateImage = view.findViewById(R.id.updateImage);
        updateTitle = view.findViewById(R.id.updateTitle);
        updateInstructions = view.findViewById(R.id.updateInstructions); // Fix for missing EditText for Description
        updateIngredients = view.findViewById(R.id.updateIngredients);

        // Ensure bundle is not null and all keys are available
        Bundle bundle = getArguments();
        if (bundle != null) {
            imageUrl = bundle.getString("Image");
            key = bundle.getString("key");
            oldImageURL = bundle.getString("Image");
            title = bundle.getString("Title");
            ingredients = bundle.getString("Ingredient");
            instructions = bundle.getString("instructions");

            if (oldImageURL != null) {
                Glide.with(getContext()).load(oldImageURL).into(updateImage);
            } else {
                Log.e(TAG, "Image URL is null");
            }

            updateTitle.setText(bundle.getString("Title"));
            updateInstructions.setText(bundle.getString("instructions"));
            updateIngredients.setText(bundle.getString("Ingredient"));
        } else {
            Log.e(TAG, "Bundle is null");
        }

        // Initialize ActivityResultLauncher for image selection
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            uri = data.getData(); // Get the selected image URI
                            updateImage.setImageURI(uri); // Display the selected image
                        }
                    } else {
                        // Fallback to oldImageURL if no new image is selected
                        if (oldImageURL != null && !oldImageURL.isEmpty()) {
                            Glide.with(getContext())
                                    .load(oldImageURL)
                                    .into(updateImage);
                        }
                    }
                }
        );

        // Database reference initialization
        databaseReference = FirebaseDatabase.getInstance().getReference("mealmate_recipes").child(key);

        // Set up image selection on click
        updateImage.setOnClickListener(view1 -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        // Set up button to save data
        updateButton.setOnClickListener(view12 -> {
            saveData();

            // Navigate back to the previous fragment or any fragment you want
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new MyRecipeFragment()); // replace with your target fragment
            fragmentTransaction.addToBackStack(null); // Optional: add to back stack to allow back navigation
            fragmentTransaction.commit();
        });

        return view;
    }

    private void saveData() {
        if (uri == null) {
            uri = Uri.parse(oldImageURL);
        }
        if (uri != null && !uri.toString().isEmpty()) {
            uploadImage(uri);
        } else {
            Toast.makeText(getContext(), "Invalid URI", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri imageUri) {
        dialog = new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setView(R.layout.progress_layout)
                .create();
        dialog.show();

        File tempFile = new File(requireContext().getCacheDir(), "temp_image.jpg");

        Glide.with(getContext())
                .asBitmap()
                .load(imageUri)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try (FileOutputStream out = new FileOutputStream(tempFile)) {
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            Uri localUri = Uri.fromFile(tempFile);

                            StorageReference storageReference = FirebaseStorage.getInstance()
                                    .getReference("mealmate_recipes")
                                    .child(Objects.requireNonNull(localUri.getLastPathSegment()));

                            storageReference.putFile(localUri).addOnSuccessListener(taskSnapshot -> {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(newImageUri -> {
                                    updateData(newImageUri.toString());
                                    dialog.dismiss();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to retrieve image URL", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                });
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Error saving image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    public void updateData(String newImageUrl) {
        title = updateTitle.getText().toString().trim();
        ingredients = updateIngredients.getText().toString().trim();

        // Create the list of ingredients (as List<String>)
        List<String> ingredientList = parseIngredients(ingredients);
        List<String> instructionsList = Collections.singletonList(updateInstructions.getText().toString().trim());

        Recipe recipe = new Recipe(title, newImageUrl, ingredientList, instructionsList.toString(), null);

        // Save the updated recipe to the database
        databaseReference.setValue(recipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Check if oldImageURL is valid before attempting to delete
                if (oldImageURL != null && !oldImageURL.isEmpty()) {
                    // Create a reference for the old image using Firebase Storage
                    try {
                        StorageReference oldImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);

                        // Delete the old image from Firebase Storage
                        oldImageReference.delete().addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Recipe updated successfully", Toast.LENGTH_SHORT).show();
                            assert getActivity() != null;
                            getActivity().onBackPressed(); // Close the fragment
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to delete old image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "Invalid Firebase Storage URL: " + oldImageURL, e);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Failed to update recipe", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private List<String> parseIngredients(String input) {
        List<String> ingredientList = new ArrayList<>();
        if (input.isEmpty()) return ingredientList;

        String[] ingredientLines = input.split("\n");
        for (String line : ingredientLines) {
            ingredientList.add(line.trim()); // Add each ingredient as a string
        }
        return ingredientList;
    }
}
