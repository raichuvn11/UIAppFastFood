package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.NotificationAdapter;
import com.example.uiappfastfood.model.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private ListView listView;
    private NotificationAdapter notificationAdapter;
    private List<NotificationItem> notificationItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        listView = findViewById(R.id.lv_notification);

        notificationItemList = new ArrayList<>();
        notificationItemList.add(new NotificationItem(R.drawable.ic_discount, "Special Discount", "Special discount description"));
        notificationItemList.add(new NotificationItem(R.drawable.ic_discount, "Special Discount", "Special discount description"));
        notificationItemList.add(new NotificationItem(R.drawable.ic_discount, "Special Discount", "Special discount description"));

        notificationAdapter = new NotificationAdapter(this, notificationItemList);
        listView.setAdapter(notificationAdapter);

        // forward to cart activity
        findViewById(R.id.llCart).setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // forward to profile activity
        findViewById(R.id.llProfile).setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, ProfileSettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        //back to home activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            // back to home activity
            Toast.makeText(this, "back to home page", Toast.LENGTH_SHORT).show();
        });
    }
}
