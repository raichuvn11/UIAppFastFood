package com.example.uiappfastfood.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappfastfood.R;

public class PersonalDataActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);

        //back to previous activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            finish();
        });
    }
}
