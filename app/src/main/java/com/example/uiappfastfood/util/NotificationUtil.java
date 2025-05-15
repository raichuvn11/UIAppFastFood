package com.example.uiappfastfood.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.uiappfastfood.model.NotificationItem;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationUtil {
    private static final String PREF_NAME = "notification_pref";
    // Trả về key lưu danh sách thông báo theo userId
    private static String getNotificationKey(long userId) {
        return "notification_list_user_" + userId;
    }

    public static void saveNotification(Context context, NotificationItem item) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        long userId = sharedPrefManager.getUserId();

        // Lấy danh sách cũ
        String json = prefs.getString(getNotificationKey(userId), null);
        Type type = new TypeToken<ArrayList<NotificationItem>>() {}.getType();
        List<NotificationItem> list = gson.fromJson(json, type);
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(0, item); // Thêm thông báo mới vào đầu danh sách

        // Lưu lại danh sách
        String updatedJson = gson.toJson(list);
        prefs.edit().putString(getNotificationKey(userId), updatedJson).apply();
    }

    public static List<NotificationItem> getNotificationList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        long userId = sharedPrefManager.getUserId();
        // Lấy danh sách thông báo
        String json = prefs.getString(getNotificationKey(userId), null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<ArrayList<NotificationItem>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static int getNewNotification(Context context) {
        List<NotificationItem> list = getNotificationList(context);
        int count = 0;
        for (NotificationItem item : list) {
            if (!item.isRead()) {
                count++;
            }
        }
        return count;
    }

    public static void updateNotificationStatus(Context context, NotificationItem updatedItem) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        long userId = sharedPrefManager.getUserId();
        // Lấy danh sách thông báo
        String json = prefs.getString(getNotificationKey(userId), null);
        Type type = new TypeToken<ArrayList<NotificationItem>>() {}.getType();
        List<NotificationItem> list = gson.fromJson(json, type);
        if (list == null) return;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTimeStamp() == updatedItem.getTimeStamp()) {
                list.set(i, updatedItem);
                break;
            }
        }

        String updatedJson = gson.toJson(list);
        prefs.edit().putString(getNotificationKey(userId), updatedJson).apply();
    }

    public static void clearNotificationList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        long userId = sharedPrefManager.getUserId();

        prefs.edit().remove(getNotificationKey(userId)).apply();
    }

    public static void removeReadNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        long userId = sharedPrefManager.getUserId();

        String json = prefs.getString(getNotificationKey(userId), null);
        if (json == null) return;

        Type type = new TypeToken<ArrayList<NotificationItem>>() {}.getType();
        List<NotificationItem> list = new Gson().fromJson(json, type);
        if (list == null) return;

        // Giữ lại những thông báo chưa đọc
        List<NotificationItem> filtered = new ArrayList<>();
        for (NotificationItem item : list) {
            if (!item.isRead()) {
                filtered.add(item);
            }
        }
        // Ghi đè lại danh sách đã lọc
        String updatedJson = new Gson().toJson(filtered);
        prefs.edit().putString(getNotificationKey(userId), updatedJson).apply();
    }


}
