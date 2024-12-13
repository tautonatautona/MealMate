package com.example.mealmate.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mealmate.Model.Recipe;
import com.example.mealmate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdateRecipeFragment extends Fragment {

    private static final String TAG = "UpdateRecipeFragment";

    private ImageView updateImage;
    private Button updateButton;
    private EditText updateDesc, updateTitle, updateIngredients;
    private String title, desc, ingredients;
    private String imageUrl;
    private String key, oldImageURL;
    private Uri uri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private AlertDialog dialog;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_recipe, container, false);

        updateButton = view.findViewById(R.id.updateButton);
        updateDesc = view.findViewById(R.id.updateDesc);
        updateImage = view.findViewById(R.id.updateImage);
        updateTitle = view.findViewById(R.id.updateTitle);
        updateIngredients = view.findViewById(R.id.updateIngredients);

        // Ensure bundle is not null and all keys are available
        Bundle bundle = getArguments();
        if (bundle != null) {
            imageUrl = bundle.getString("Image");
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");

            if (oldImageURL != null) {
                Glide.with(getContext()).load(oldImageURL).into(updateImage);
            } else {
                Log.e(TAG, "Image URL is null");
            }

            updateTitle.setText(bundle.getString("Title"));
            updateDesc.setText(bundle.getString("Description"));
            updateIngredients.setText(bundle.getString("Ingredients"));
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes").child(key);

        // Set up image selection on click
        updateImage.setOnClickListener(view1 -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        /// Set up button to save data
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

    public void saveData() {
        if (uri == null) {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        storageReference = FirebaseStorage.getInstance().getReference().child("Recipe Images").child(Objects.requireNonNull(uri.getLastPathSegment()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(url -> {
                imageUrl = url.toString();
                updateData();
                dialog.dismiss();
            });
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
        });
    }

    public void updateData() {
        title = updateTitle.getText().toString().trim();
        desc = updateDesc.getText().toString().trim();
        ingredients = updateIngredients.getText().toString().trim();

        // Create the list of ingredients (empty or parsed as needed)
        List<Recipe.InstructionIngredient> ingredientList = parseIngredients(ingredients);

        // Create a list of instructions (empty if not updated)
        List<Recipe.RecipeInstruction> instructionList = new ArrayList<>();  // Assuming you can add instructions similarly

        // Create a new Recipe object
        Recipe recipe = new Recipe(key, title, imageUrl, desc, key, ingredientList, "", instructionList);

        // Save the updated recipe to the database
        databaseReference.setValue(recipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                reference.delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Recipe updated successfully", Toast.LENGTH_SHORT).show();
                    assert getActivity() != null;
                    getActivity().onBackPressed(); // Close the fragment
                });
            } else {
                Toast.makeText(getContext(), "Failed to update recipe", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private List<Recipe.InstructionIngredient> parseIngredients(String input) {
        List<Recipe.InstructionIngredient> ingredientList = new ArrayList<>();
        if (input.isEmpty()) return ingredientList;

        String[] ingredientLines = input.split("\n");
        for (String line : ingredientLines) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String quantity = parts[2].trim();
                String unit = parts[3].trim();
                ingredientList.add(new Recipe.InstructionIngredient(id, name, quantity, unit));
            }
        }
        return ingredientList;
    }
}
