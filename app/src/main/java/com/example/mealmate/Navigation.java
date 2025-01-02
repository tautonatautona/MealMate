package com.example.mealmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mealmate.Fragments.HomePageFragment;
import com.example.mealmate.Fragments.GroceryListFragment;
import com.example.mealmate.Fragments.MyRecipeFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Navigation extends AppCompatActivity {

    private static final String FALLBACK_USER_NAME = "Guest";

    private TabLayout tabLayout;
    private Fragment homeFragment, myRecipesFragment, groceryListFragment;
    private FragmentManager fragmentManager;
    private final String[] labels = new String[]{"Home", "MyRecipes", "Account"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_navigation);
        setupWindowInsets();

        // Initialize UI components
        TextView userName = findViewById(R.id.userName);
        ImageView profileImage = findViewById(R.id.profileImage);

        setUserName(userName);
        setUserProfileImage(profileImage);
        
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        init();
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

        // Redirect to LoginActivity
        Intent intent = new Intent(Navigation.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish(); // Close the current activity
    }


    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.navigation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setUserName(TextView userName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            userName.setText(displayName != null && !displayName.isEmpty() ? displayName : currentUser.getEmail());
        } else {
            userName.setText(FALLBACK_USER_NAME);
        }
    }

    private void setUserProfileImage(ImageView profileImage) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getPhotoUrl() != null) {
            String photoUrl = currentUser.getPhotoUrl().toString();

            // Load the profile image using Glide
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.profile_image) // Placeholder while loading
                    .error(R.drawable.profile_image)      // Default image on error
                    .apply(RequestOptions.circleCropTransform())  // Crop image into a circle
                    .into(profileImage);
        } else {

            // Set a default image if no profile URL is available
            profileImage.setImageResource(R.drawable.profile_image);
        }

    }
    

    private void init() {

        Toolbar tb = findViewById(R.id.toolBar);
        setSupportActionBar(tb);

        tabLayout = findViewById(R.id.tab_layout);
        fragmentManager = getSupportFragmentManager();

        // Initialize fragments
        homeFragment = new HomePageFragment();
        myRecipesFragment = new MyRecipeFragment();
        groceryListFragment = new GroceryListFragment();

        // Set default fragment
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();

        // Setup TabLayout and icons
        setupTabLayout();
        configureTabIcons();
    }

    private void setupTabLayout() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void configureTabIcons() {

        TabLayout.Tab homeTab = tabLayout.newTab().setIcon(R.drawable.ic_home);
        TabLayout.Tab recipeTab = tabLayout.newTab().setIcon(R.drawable.ic_recipe);
        TabLayout.Tab groceryListTab = tabLayout.newTab().setIcon(R.drawable.shopping_basket);
        //.setText(labels[2]) to set tabName

        tabLayout.addTab(homeTab);
        tabLayout.addTab(recipeTab);
        tabLayout.addTab(groceryListTab);
    }

    private void switchFragment(int position) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (position) {
            case 0:
                transaction.replace(R.id.fragment_container, homeFragment);
                break;
            case 1:
                transaction.replace(R.id.fragment_container, myRecipesFragment);
                break;
            case 2:
                transaction.replace(R.id.fragment_container, groceryListFragment);
                break;
        }

        transaction.commit();
    }
}
