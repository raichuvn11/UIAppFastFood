package com.example.uiappfastfood.activity;

import static java.security.AccessController.getContext;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappfastfood.DTO.request.GoogleLoginRequest;
import com.example.uiappfastfood.DTO.request.RegisterRequest;
import com.example.uiappfastfood.DTO.response.GenericResponse;
import com.example.uiappfastfood.DTO.response.LoginResponse;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {



    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;
    private static final String CLIENT_ID = "288243577014-798i3784366217gjfi218kdb53gsurhd.apps.googleusercontent.com"; // Sử dụng Client ID từ Google Developer Console
    private EditText etEmail, etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;

    private ApiService apiService;
    private TextView tvRegister, tvSignIn;
    private ImageView ivGoogle,ivTogglePassword, ivToggleConfirmPassword;
    private Dialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        initializeViews();

        setUpPasswordToggle();

        setUpRegisterButton();


        setUpLoginNavigation();

        setUpGoogleLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();
                sendIdTokenToServer(idToken);
            } catch (ApiException e) {
                Log.e("GoogleLogin", "ApiException code: " + e.getStatusCode(), e);
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveTokenToSharedPreferences(Long id) {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(RegisterActivity.this);
        sharedPrefManager.saveUserId(id,"google");
    }
    private void sendIdTokenToServer(String idToken) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<LoginResponse> call = apiService.loginWithGoogle(new GoogleLoginRequest(idToken));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("LoginResponse", idToken);
                if (response.isSuccessful() && response.body() != null) {
                    saveTokenToSharedPreferences(response.body().getData().getUserId());
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("SendIdTokenToServer", "Error: " + t.getMessage());

                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showLoading() {
        progressDialog = new Dialog(RegisterActivity.this);
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void initializeViews() {
        // Khởi tạo GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        ivGoogle = findViewById(R.id.ivGoogle);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        tvSignIn = findViewById(R.id.tvSignIn);
    }

    private void setUpPasswordToggle() {

        ivTogglePassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                return true;
            }
        });
        ivToggleConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
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
    private void setUpRegisterButton() {
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validateInputs(email, username, password, confirmPassword)) {
                RegisterRequest registerRequest = new RegisterRequest(username, email, password);
                registerUser(registerRequest);
            }
        });
    }

    private boolean validateInputs(String email, String username, String password, String confirmPassword) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm Password is required");
            etConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void registerUser(RegisterRequest registerRequest) {
        showLoading();
        apiService.register(registerRequest).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse registerResponse = response.body();
                    if ("success".equals(registerResponse.getStatus())) {
                        navigateToOTPRegisterActivity(registerResponse);
                    } else {
                        hideLoading();
                        Toast.makeText(RegisterActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hideLoading();
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                hideLoading();
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToOTPRegisterActivity(GenericResponse registerResponse) {
        Intent intent = new Intent(RegisterActivity.this, OTPRegisterActivity.class);
        intent.putExtra("email", etEmail.getText().toString().trim());
        intent.putExtra("username", etUsername.getText().toString().trim());
        intent.putExtra("password", etPassword.getText().toString().trim());
        Log.e("RegisterActivity", "Password: " + etPassword.getText().toString().trim());
        hideLoading();
        startActivity(intent);
    }

    private void setUpLoginNavigation() {
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setUpGoogleLogin() {
        ivGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }
}