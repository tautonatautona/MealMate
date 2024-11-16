package com.example.mealmate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mealmate.Fragments.HomePageFragment;
import com.example.mealmate.Fragments.MyAccountFragment;
import com.example.mealmate.Fragments.MyRecipeFragment;
import com.example.mealmate.Utils.ImageUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Navigation extends AppCompatActivity {

    private Toolbar tb;

    private TabLayout tabLayout;

    private TextView userName;

    private ViewPager2 viewPager;
    ViewPagerFragmentAdapter adapter;

    private final String[] labels = new String[] {"Home","MyRecipes","Account"};


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_navigation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.navigation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the UI components
        userName = findViewById(R.id.userName);


        // Get the current logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Get the user's display name
            String displayName = currentUser.getDisplayName();

            // Check if the display name is not null or empty
            if (displayName != null && !displayName.isEmpty()) {
                // Set the display name to the TextView
                userName.setText("Welcome, " + displayName);

            }
            else {
                // If no display name, fallback to email or some default text
                userName.setText("Welcome, " + currentUser.getEmail());
            }
        }
        else {
            // Handle the case when no user is logged in (if needed)
            userName.setText("Welcome, Guest");
        }

        init();

        // Bind and set TabLayout to ViewPager2 and set icons for each tab
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Set icons for each tab
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.ic_home); // Replace with your actual icon resource
                    break;
                case 1:
                    tab.setIcon(R.drawable.ic_recipe); // Replace with your actual icon resource
                    break;
                case 2:
                    tab.setIcon(R.drawable.ic_account); // Replace with your actual icon resource
                    break;
            }
        }).attach();

        viewPager.setCurrentItem(1, false);
    }

    private void init(){
        tb = findViewById(R.id.ToolBar);
        setSupportActionBar(tb);

        // Initialize TabLayout
        tabLayout = findViewById(R.id.tab_layout);
        // Initialize ViewPager2
        viewPager = findViewById(R.id.view_pager);
        // Create adapter instance
        adapter = new ViewPagerFragmentAdapter(this);
        // Set adapter to ViewPager2
        viewPager.setAdapter(adapter);

        getSupportActionBar().setElevation(0);
    }

    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new HomePageFragment();
                case 1:
                    return new MyRecipeFragment();
                case 2:
                    return new MyAccountFragment();
            }
            return new HomePageFragment();
        }

        @Override
        public int getItemCount() {
            return labels.length;
        }
    }
}
