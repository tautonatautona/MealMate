package com.example.mealmate.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import com.example.mealmate.Navigation;
import com.example.mealmate.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class RecipeDetailFragment extends Fragment {

    TextView detailDesc, detailTitle;
    ImageView detailImage;
    FloatingActionButton deleteButton, editButton;
    String key = "";
    String imageUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        detailDesc = view.findViewById(R.id.detailDesc);
        detailImage = view.findViewById(R.id.detailImage);
        detailTitle = view.findViewById(R.id.detailTitle);
        deleteButton = view.findViewById(R.id.deleteButton);
        editButton = view.findViewById(R.id.editButton);

        Bundle bundle = getArguments();

        if (bundle != null){
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(imageUrl).into(detailImage);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("mealmate_recipes");
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);

                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), Navigation.class));
                    }
                });
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new instance of UpdateRecipeFragment
                UpdateRecipeFragment updateRecipeFragment = new UpdateRecipeFragment();

                // Create a Bundle to hold the data
                Bundle bundle = new Bundle();
                bundle.putString("Title", detailTitle.getText().toString());
                bundle.putString("Description", detailDesc.getText().toString());
                bundle.putString("Image", imageUrl);
                bundle.putString("Key", key);

                // Set the arguments for the fragment
                updateRecipeFragment.setArguments(bundle);

                // Replace the current fragment with the UpdateRecipeFragment
                Objects.requireNonNull(requireActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.navigation, updateRecipeFragment) // Use your actual fragment container ID
                        .addToBackStack(null) // Optional: add to back stack for navigation
                        .commit();
            }
        });


        return view;
    }
}
