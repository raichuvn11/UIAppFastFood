package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappfastfood.bottom.ForgotPasswordBottomSheet;
import com.example.uiappfastfood.R;

public class ForgotActivity extends AppCompatActivity {

    private EditText etEmail;
    private TextView tvSignIn;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot);

        etEmail = findViewById(R.id.etEmail);
        btnContinue = findViewById(R.id.btnContinue);
        tvSignIn = findViewById(R.id.tvSignIn);

        tvSignIn.setOnClickListener(OnClickListener -> {
            Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnContinue.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                // Truyền email xuống BottomSheet
                ForgotPasswordBottomSheet bottomSheet = ForgotPasswordBottomSheet.newInstance(email);
                bottomSheet.show(getSupportFragmentManager(), "ForgotPasswordBottomSheet");
            }
        });
    }
}
