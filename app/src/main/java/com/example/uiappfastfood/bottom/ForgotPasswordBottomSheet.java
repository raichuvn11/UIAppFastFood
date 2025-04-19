package com.example.uiappfastfood.bottom;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.uiappfastfood.DTO.request.EmailRequest;
import com.example.uiappfastfood.DTO.response.GenericResponse;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.activity.OTPForgotActivity;
import com.example.uiappfastfood.activity.OTPRegisterActivity;
import com.example.uiappfastfood.activity.RegisterActivity;
import com.example.uiappfastfood.api.ApiService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.uiappfastfood.api.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_EMAIL = "email";
    private String email;
    private LinearLayout optionEmail;
    private ImageView checkEmail;
    private boolean isEmailSelected = false;
    private Dialog progressDialog;



    public static ForgotPasswordBottomSheet newInstance(String email) {
        ForgotPasswordBottomSheet fragment = new ForgotPasswordBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }
    private void navigateToOTPForgotActivity(GenericResponse registerResponse) {
        Intent intent = new Intent(getContext(), OTPForgotActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        dismiss();
    }

    private void showLoading() {
        progressDialog = new Dialog(getContext());
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

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forgot_password_bottom, container, false);
        optionEmail = view.findViewById(R.id.optionEmail);
        checkEmail = view.findViewById(R.id.checkEmail);

        checkEmail.setVisibility(View.GONE);
        isEmailSelected = false;

        optionEmail.setOnClickListener(v -> {
            isEmailSelected = !isEmailSelected;
            checkEmail.setVisibility(isEmailSelected ? View.VISIBLE : View.GONE);
        });

        if (getArguments() != null) {
            email = getArguments().getString(ARG_EMAIL);
        }

        TextView edtEmail = view.findViewById(R.id.edtEmail);
        Button btnSendOtp = view.findViewById(R.id.btnContinue);

        edtEmail.setText(email);
        btnSendOtp.setOnClickListener(v -> {
            if (!isEmailSelected) {
                Toast.makeText(getContext(), "Please select a method to reset password", Toast.LENGTH_SHORT).show();
                return;
            }

            String enteredEmail = email != null ? email.trim() : "";
            if (enteredEmail.isEmpty()) {
                Toast.makeText(getContext(), "Email is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            showLoading();

            EmailRequest request = new EmailRequest(enteredEmail);
            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Call<GenericResponse> call = apiService.sendResetOtp(request);

            call.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        GenericResponse registerResponse = response.body();
                        if ("success".equals(registerResponse.getStatus())) {
                            navigateToOTPForgotActivity(registerResponse);
                        } else {
                            Toast.makeText(getContext(), registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to send OTP", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    hideLoading();
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

}
