package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappfastfood.R;

public class ProfileSettingActivity extends AppCompatActivity {
    private LinearLayout btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        btnSignOut = findViewById(R.id.btn_signout);

        btnSignOut.setOnClickListener(view -> showSignOutDialog());

        //back to previous activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            finish();
        });

        //open personal data activity
        findViewById(R.id.personal_data_item).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileSettingActivity.this, PersonalDataActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

    }

    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_signout, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_confirm_signout).setOnClickListener(v -> {
            // sign out logic
            dialog.dismiss();
        });
    }
}
