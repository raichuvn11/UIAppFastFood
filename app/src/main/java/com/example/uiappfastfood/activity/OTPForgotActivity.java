package com.example.uiappfastfood.activity;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappfastfood.DTO.request.CheckOTPRequest;
import com.example.uiappfastfood.DTO.request.EmailRequest;
import com.example.uiappfastfood.DTO.request.ResetPasswordRequest;
import com.example.uiappfastfood.DTO.request.VerifyOtpRequest;
import com.example.uiappfastfood.DTO.response.GenericResponse;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.api.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPForgotActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private Button btnContinue;
    private ImageView btnBack;
    private TextView txtResend, txtTimer, txtEmail;
    private String otpCode = ""; // Để lưu mã OTP
    private String email;
    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_reset);

        // Ánh xạ các phần tử trong layout
        initializeViews();

        setOtpAutoTab();

        btnBack.setOnClickListener(v -> onBackPressed());
        // Get email from intent
        email = getIntent().getStringExtra("email");

        // Set email to the TextView
        txtEmail.setText(email);

        // Xử lý sự kiện "Continue"
        btnContinue.setOnClickListener(v -> onContinueClicked());

        // Xử lý sự kiện "Resend"
        txtResend.setOnClickListener(v -> onResendClicked());

        // Đặt bộ đếm thời gian đếm ngược (ví dụ 1 phút)
        startCountdownTimer();
    }

    private void initializeViews() {
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);
        txtEmail = findViewById(R.id.txtEmail);
        btnContinue = findViewById(R.id.btnContinue);
        txtResend = findViewById(R.id.txtResend);
        txtTimer = findViewById(R.id.txtTimer);
        btnBack = findViewById(R.id.btnBack);
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

    private void onContinueClicked() {
        otpCode = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() +
                otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();

        if (otpCode.length() == 6) {
            verifyOTP(otpCode);
        } else {
            Toast.makeText(OTPForgotActivity.this, "Please enter a valid OTP.", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyOTP(String otpCode) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        CheckOTPRequest request = new CheckOTPRequest(email,otpCode);

        // Gọi API kiểm tra OTP
        Call<GenericResponse> call = apiService.checkOtp(request);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse genericResponse = response.body();
                    if ("success".equals(genericResponse.getStatus())) {
                        onOTPVerified();
                    } else {
                        Toast.makeText(OTPForgotActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OTPForgotActivity.this, "Failed to verify OTP. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(OTPForgotActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onOTPVerified() {
        Intent intent = new Intent(OTPForgotActivity.this, ResetPasswordActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("otp", otpCode);
        startActivity(intent);
        finish();
    }

    private void onResendClicked() {
        resendOTP();
        startCountdownTimer();
    }

    private void resendOTP() {

        EmailRequest request = new EmailRequest(email);
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<GenericResponse> call = apiService.sendResetOtp(request);

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse registerResponse = response.body();
                    if ("success".equals(registerResponse.getStatus())) {
                        Toast.makeText(OTPForgotActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OTPForgotActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OTPForgotActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(OTPForgotActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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


}
