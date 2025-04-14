package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.util.NotificationUtil;

public class ProfileSettingActivity extends AppCompatActivity {

    TextView tvNotiCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_setting);

        tvNotiCount = findViewById(R.id.tv_notiCount);
        int unreadCount = NotificationUtil.getNewNotification(this);
        if (unreadCount > 0) {
            tvNotiCount.setText(String.valueOf(unreadCount));
        } else {
            tvNotiCount.setVisibility(View.GONE);
        }

        // sign out
        findViewById(R.id.btn_signout).setOnClickListener(view -> showSignOutDialog());

        // back to previous activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            finish();
        });

        // forward to cart activity
        findViewById(R.id.llCart).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileSettingActivity.this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // forward to notification activity
        findViewById(R.id.llNotification).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileSettingActivity.this, NotificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        //open personal data activity
        findViewById(R.id.personal_data_item).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileSettingActivity.this, PersonalDataActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        //open favourite food activity
        findViewById(R.id.btn_favouriteFood).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileSettingActivity.this, FavouriteFoodActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        //open tracking order activity
        findViewById(R.id.btn_trackingOrder).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileSettingActivity.this, OrderStatusActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        //open order address activity
        findViewById(R.id.btn_orderAddress).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileSettingActivity.this, LocationActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();

        if (tvNotiCount != null) {
            int unreadCount = NotificationUtil.getNewNotification(this);

            if (unreadCount > 0) {
                tvNotiCount.setVisibility(View.VISIBLE);
                tvNotiCount.setText(String.valueOf(unreadCount));
            } else {
                tvNotiCount.setVisibility(View.GONE);
            }
        }
    }
}
