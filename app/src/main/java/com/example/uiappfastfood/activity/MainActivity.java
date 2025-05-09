package com.example.uiappfastfood.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.uiappfastfood.fragment.HomeFragment;
import com.example.uiappfastfood.fragment.CartFragment;
import com.example.uiappfastfood.fragment.NotificationFragment;
import com.example.uiappfastfood.fragment.ProfileSettingFragment;
import com.example.uiappfastfood.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        // Mặc định hiển thị HomeFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
        }

        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                }
                if(item.getItemId() == R.id.nav_cart){
                    selectedFragment = new CartFragment();
                }
                if(item.getItemId() == R.id.nav_profile){
                    selectedFragment = new ProfileSettingFragment();
                }
                if(item.getItemId() == R.id.nav_notification){
                    selectedFragment = new NotificationFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, selectedFragment)
                            .commit();
                }

                return true;
            }
        });
    }
}