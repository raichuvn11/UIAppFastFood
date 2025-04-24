package com.example.uiappfastfood.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.model.NotificationItem;
import com.example.uiappfastfood.util.NotificationUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCM Service", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("FCM Service", "Data Payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }
    }

    private void handleDataMessage(Map<String, String> data) {
        String title = data.get("title");
        String body = data.get("body");
        String type = data.get("type");

        if (title != null && body != null) {
            showNotification(title, body, type);
        }
    }

    private void showNotification(String title, String message, String type) {

        NotificationItem item = null;
        if ("order-update".equals(type)) {
            item = new NotificationItem(R.drawable.ic_promo, title, message, System.currentTimeMillis(), type);
        } else if ("promotion".equals(type)) {
            item = new NotificationItem(R.drawable.ic_discount, title, message, System.currentTimeMillis(), type);
        }
        else{
            item = new NotificationItem(R.drawable.ic_notification, title, message, System.currentTimeMillis(), type);
        }
        NotificationUtil.saveNotification(getApplicationContext(), item);

        String channelId = "channel_id";
        String channelName = "Channel Name";

        // Tạo Notification Channel (cho Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_promo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Kiểm tra quyền POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.e("FCM Service", "Permission POST_NOTIFICATIONS not granted");
                // Bạn có thể yêu cầu quyền hoặc hiển thị thông báo lỗi
                return;
            }
        }

        // Gửi thông báo với ID duy nhất (dùng System.currentTimeMillis() để tránh trùng lặp)
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }
}


