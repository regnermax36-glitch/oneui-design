package com.samsung.android.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sec.sesl.tester.R;

/**
 * Samsung Music Player Splash Screen with One UI 10.0 Design
 * Features Samsung branding and smooth animations
 */
public class SplashActivity extends AppCompatActivity {
    
    private static final int SPLASH_DURATION = 3000; // 3 seconds
    
    private ImageView logoImageView;
    private TextView brandingTextView;
    private TextView versionTextView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        initializeViews();
        startAnimations();
        scheduleMainActivity();
    }
    
    private void initializeViews() {
        logoImageView = findViewById(R.id.logo_image);
        brandingTextView = findViewById(R.id.branding_text);
        versionTextView = findViewById(R.id.version_text);
    }
    
    private void startAnimations() {
        // Logo fade in animation
        Animation logoFadeIn = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in);
        logoImageView.startAnimation(logoFadeIn);
        
        // Branding text slide up animation
        Animation textSlideUp = AnimationUtils.loadAnimation(this, R.anim.splash_slide_up);
        brandingTextView.startAnimation(textSlideUp);
        
        // Version text fade in with delay
        Animation versionFadeIn = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in_delayed);
        versionTextView.startAnimation(versionFadeIn);
    }
    
    private void scheduleMainActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            
            // Samsung One UI 10.0 transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DURATION);
    }
    
    @Override
    public void onBackPressed() {
        // Disable back button during splash
    }
}
