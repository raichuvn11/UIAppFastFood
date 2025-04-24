package com.example.uiappfastfood.util;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.model.DeviceTokenRequest;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;
public class DeviceTokenUtil {
    public static void getDeviceToken(Long userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        Log.d("FCM Token", "Device Token: " + token);
                        // Gửi token này về server
                        sendTokenToServer(userId, token);
                    } else {
                        // Xử lý lỗi
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                    }
                });
    }

    public static void sendTokenToServer(Long userId, String token) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        DeviceTokenRequest request = new DeviceTokenRequest(userId, token);

        Call<Void> call = apiService.updateDeviceToken(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("FCM", "Token sent to server successfully");
                } else {
                    Log.e("FCM", "Failed to send token: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FCM", "Error sending token: " + t.getMessage());
            }
        });
    }
}
