package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappfastfood.DTO.request.ResetPasswordRequest;
import com.example.uiappfastfood.DTO.response.GenericResponse;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.api.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private ApiService apiService;
    private ImageView btnBack, ivShowPassWord, ivShowCofirmPassWord;
    private TextView tvPasswordHint, tvConfirmHint;
    private EditText etNewPassword, etConfirmPassword;
    private Button btnVerifyAccount;
    private String email, otpCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Initialize views
        initializeViews();
        setUpPasswordToggle();

        // Get email and OTP from intent
        email = getIntent().getStringExtra("email");
        otpCode = getIntent().getStringExtra("otp");

        // Set onClick listener for Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Set onClick listener for Verify Account button
        btnVerifyAccount.setOnClickListener(v -> handleResetPassword());

        // Set TextWatchers for password fields
    }

    private void setUpPasswordToggle() {

        ivShowPassWord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                return true;
            }
        });
        ivShowCofirmPassWord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                return true;
            }
        });
    }
    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvPasswordHint = findViewById(R.id.tvPasswordHint);
        tvConfirmHint = findViewById(R.id.tvConfirmHint);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnVerifyAccount = findViewById(R.id.btnVerifyAccount);
        ivShowPassWord = findViewById(R.id.ivShowPassWord);
        ivShowCofirmPassWord = findViewById(R.id.ivShowCofirmPassWord);
    }


    private boolean validateInputs(String newPassword, String confirmPassword) {
        // Check if new password is empty
        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return false;
        }

        // Check if confirm password is empty
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm password is required");
            etConfirmPassword.requestFocus();
            return false;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        // Check if password length is valid
        if (newPassword.length() < 8) {
            etNewPassword.setError("Password must be at least 8 characters");
            etNewPassword.requestFocus();
            return false;
        }

        return true;
    }
    private void handleResetPassword() {
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (validateInputs(newPassword, confirmPassword)) {
            // Make the API call to reset the password if inputs are valid
            resetPassword(newPassword);
        }
    }

    private void resetPassword(String newPassword) {
        ResetPasswordRequest request = new ResetPasswordRequest(email, otpCode, newPassword);

        apiService.resetPassword(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse responseBody = response.body();
                    if ("success".equals(responseBody.getStatus())) {
                        showChangePasswordSuccessBottomSheet();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, responseBody.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Optionally, cancel any background tasks if needed
    }
    private void showChangePasswordSuccessBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.password_change_bottom, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();

        Button btnContinue = view.findViewById(R.id.btnVerifyAccount);
        btnContinue.setOnClickListener(v -> {
            dialog.dismiss();
            // Chuyển đến màn hình đăng nhập
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}