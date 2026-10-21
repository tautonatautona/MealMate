package com.example.mealmate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 6000; // Reduced to 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Photo background is dark, so keep the system bar icons light
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_splash);

        // Handle window insets for immersive experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Start the Lottie animation
        LottieAnimationView animationView = findViewById(R.id.animationView);
        animationView.playAnimation();

        // Navigate to the next screen after the splash delay
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, SPLASH_DISPLAY_LENGTH);
    }

    private void navigateToNextScreen() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Navigate to HomeScreen
            startActivity(new Intent(SplashActivity.this, Navigation.class));
        } else {
            // Navigate to LoginScreen
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish(); // Close SplashActivity
    }
}
