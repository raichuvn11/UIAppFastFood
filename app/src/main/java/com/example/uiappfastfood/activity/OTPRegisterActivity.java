package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappfastfood.DTO.request.RegisterRequest;
import com.example.uiappfastfood.DTO.request.VerifyOtpRequest;
import com.example.uiappfastfood.DTO.response.GenericResponse;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.api.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPRegisterActivity extends AppCompatActivity {

    private ApiService apiService;
    private ImageView btnBack;
    private TextView txtEmail, txtResend, txtTimer;

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private Button btnContinue;

    private String email,username,password;
    private String otpCode = "";

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_register);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        // Initialize views
        initializeViews();

        // Get email from intent
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        Log.e("OTPRegisterActivity", "Password: " + password + "Email: " + email + "Username: " + username );

        // Set email to the TextView
        txtEmail.setText(email);

        // Set the countdown timer for OTP resend
        startCountdownTimer();

        // Set onClick listener for "Back" button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Set onClick listener for "Resend" button
        txtResend.setOnClickListener(v -> resendOTP());

        // Set onClick listener for "Continue" button
        btnContinue.setOnClickListener(v -> validateOTP());

        // Set TextWatchers for OTP fields
        setOtpAutoTab();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        txtResend = findViewById(R.id.txtResend);
        txtTimer = findViewById(R.id.txtTimer);
        txtEmail = findViewById(R.id.txtEmail);
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void startCountdownTimer() {
        txtResend.setVisibility(View.GONE);

        // Countdown timer for OTP resend
        countDownTimer = new CountDownTimer(60000, 1000) { // 1 minute countdown
            public void onTick(long millisUntilFinished) {
                txtTimer.setText(String.format("%02d:%02d", millisUntilFinished / 60000, (millisUntilFinished % 60000) / 1000));
            }

            public void onFinish() {
                txtResend.setVisibility(View.VISIBLE);
                txtTimer.setText("00:00");
            }
        };
        countDownTimer.start();
    }

    private void resendOTP() {
        registerUser(new RegisterRequest(username, email, password));
        startCountdownTimer();
    }

    private void validateOTP() {
        // Gather OTP from each EditText field
        otpCode = otp1.getText().toString() + otp2.getText().toString() +
                otp3.getText().toString() + otp4.getText().toString() +
                otp5.getText().toString() + otp6.getText().toString();

        // Check if all OTP fields are filled
        if (TextUtils.isEmpty(otpCode) || otpCode.length() < 6) {
            Toast.makeText(this, "Please enter the complete OTP", Toast.LENGTH_SHORT).show();
        } else {
            // Call the API to validate OTP
            validateOTPWithServer(otpCode);
        }
    }

    private void validateOTPWithServer(String otpCode) {

        apiService.verifyOtp(new VerifyOtpRequest(username,email, password, otpCode)).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse verifyOtpResponse = response.body();
                    if ("success".equals(verifyOtpResponse.getStatus())) {
                        showRegisterSuccessBottomSheet();
                    } else {
                        Toast.makeText(OTPRegisterActivity.this, verifyOtpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OTPRegisterActivity.this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(OTPRegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOtpAutoTab() {
        // Set TextWatchers for all OTP input fields
        otp1.addTextChangedListener(createOtpTextWatcher(otp2));
        otp2.addTextChangedListener(createOtpTextWatcher(otp3));
        otp3.addTextChangedListener(createOtpTextWatcher(otp4));
        otp4.addTextChangedListener(createOtpTextWatcher(otp5));
        otp5.addTextChangedListener(createOtpTextWatcher(otp6));
        otp6.addTextChangedListener(createOtpTextWatcher(null)); // No next field after otp6
    }

    private TextWatcher createOtpTextWatcher(final EditText nextOtpField) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() == 1 && nextOtpField != null) {
                    nextOtpField.requestFocus(); // Move focus to the next field
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the countdown timer when the activity is destroyed
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void registerUser(RegisterRequest registerRequest) {
        apiService.register(registerRequest).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse registerResponse = response.body();
                    if ("success".equals(registerResponse.getStatus())) {
                        Toast.makeText(OTPRegisterActivity.this, "OTP resent successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(OTPRegisterActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(OTPRegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showRegisterSuccessBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.register_success_bottom, null);
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
