package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappfastfood.R;

public class EmptyCartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_empty_cart);

        findViewById(R.id.llNotification).setOnClickListener(v -> {
            Intent intent = new Intent(EmptyCartActivity.this, NotificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.llProfile).setOnClickListener(v -> {
            Intent intent = new Intent(EmptyCartActivity.this, ProfileSettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.cvBack).setOnClickListener(v -> {
            Toast.makeText(this, "back to home page", Toast.LENGTH_SHORT).show();
        });
    }
}
