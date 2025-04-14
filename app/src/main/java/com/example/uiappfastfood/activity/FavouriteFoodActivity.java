package com.example.uiappfastfood.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappfastfood.R;

public class FavouriteFoodActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_food);

        //back to previous activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            finish();
        });
    }
}
