package com.example.uiappfastfood.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.activity.LoginActivity;
import com.example.uiappfastfood.activity.MainActivity;
import com.example.uiappfastfood.fragment.NotificationFragment;
import com.example.uiappfastfood.model.NotificationItem;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;
import com.example.uiappfastfood.util.NotificationUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCM Service", "From: " + remoteMessage.getFrom());

        if (!remoteMessage.getData().isEmpty()) {
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
        } else {
            item = new NotificationItem(R.drawable.ic_notification, title, message, System.currentTimeMillis(), type);
        }
        NotificationUtil.saveNotification(getApplicationContext(), item);

        String channelId = "channel_id";
        String channelName = "Channel Name";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        long userId = sharedPrefManager.getUserId();

        Intent intent;
        if (userId != -1) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_promo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.e("FCM Service", "Permission POST_NOTIFICATIONS not granted");
                return;
            }
        }

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }

}


