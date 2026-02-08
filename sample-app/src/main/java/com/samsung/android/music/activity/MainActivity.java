package com.samsung.android.music.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sec.sesl.tester.R;
import com.samsung.android.music.fragment.PlayerFragment;
import com.samsung.android.music.fragment.LibraryFragment;

/**
 * Main Activity for Samsung Music Player
 * Features One UI 10.0 design with glassy navigation
 */
public class MainActivity extends AppCompatActivity {
    
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
    private BottomNavigationView bottomNavigation;
    private FragmentManager fragmentManager;
    
    // Fragments
    private PlayerFragment playerFragment;
    private LibraryFragment libraryFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        checkPermissions();
        setupNavigation();
        
        // Show player fragment by default
        if (savedInstanceState == null) {
            showPlayerFragment();
        }
    }
    
    private void initializeViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();
    }
    
    private void checkPermissions() {
        String[] permissions;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[] {
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            };
        } else {
            permissions = new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }
        
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        
        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (!allGranted) {
                Toast.makeText(this, "Permissions required for music playback", 
                             Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void setupNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_player) {
                showPlayerFragment();
                return true;
            } else if (itemId == R.id.nav_library) {
                showLibraryFragment();
                return true;
            }
            
            return false;
        });
    }
    
    private void showPlayerFragment() {
        if (playerFragment == null) {
            playerFragment = new PlayerFragment();
        }
        showFragment(playerFragment, "PlayerFragment");
    }
    
    private void showLibraryFragment() {
        if (libraryFragment == null) {
            libraryFragment = new LibraryFragment();
        }
        showFragment(libraryFragment, "LibraryFragment");
    }
    
    private void showFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // Hide all fragments
        for (Fragment frag : fragmentManager.getFragments()) {
            if (frag.isVisible()) {
                transaction.hide(frag);
            }
        }
        
        // Show or add the target fragment
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.fragment_container, fragment, tag);
        }
        
        // Samsung One UI 10.0 transition animation
        transaction.setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        );
        
        transaction.commit();
    }
    
    @Override
    public void onBackPressed() {
        // Handle back press with Samsung One UI 10.0 behavior
        if (bottomNavigation.getSelectedItemId() != R.id.nav_player) {
            bottomNavigation.setSelectedItemId(R.id.nav_player);
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
    }
}
