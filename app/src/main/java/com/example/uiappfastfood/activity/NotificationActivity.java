package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.NotificationAdapter;
import com.example.uiappfastfood.model.NotificationItem;
import com.example.uiappfastfood.util.NotificationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<NotificationItem> notificationItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.rv_notification);

        notificationItemList = NotificationUtil.getNotificationList(this);
        Collections.sort(notificationItemList, (a, b) -> Long.compare(b.getTimeStamp(), a.getTimeStamp()));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(this, notificationItemList, item -> {

            int position = notificationItemList.indexOf(item);
            if (!item.isRead()) {
                item.setRead(true);
                NotificationUtil.updateNotificationStatus(this, item);
                notificationAdapter.notifyItemChanged(position);
            }

            String body = item.getDescription();

            int selectedTab = 0;

            if (body.contains("đang giao")) {
                selectedTab = 1;
            } else if (body.contains("đã giao")) {
                selectedTab = 2;
            } else if (body.contains("hủy")) {
                selectedTab = 3;
            }

            Intent intent = new Intent(NotificationActivity.this, OrderStatusActivity.class);
            intent.putExtra("selectedTab", selectedTab);
            startActivity(intent);
        });
        recyclerView.setAdapter(notificationAdapter);

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
            overridePendingTransition(0, 0);
        });

        //back to home activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            // back to home activity
            Toast.makeText(this, "back to home page", Toast.LENGTH_SHORT).show();
        });
    }
}
