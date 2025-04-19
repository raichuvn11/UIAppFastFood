package com.example.uiappfastfood.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.uiappfastfood.model.NotificationItem;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationUtil {
    private static final String PREF_NAME = "notification_pref";
    private static final String KEY_NOTIFICATION_LIST = "notification_list";

    public static void saveNotification(Context context, NotificationItem item) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // Lấy danh sách cũ
        String json = prefs.getString(KEY_NOTIFICATION_LIST, null);
        Type type = new TypeToken<ArrayList<NotificationItem>>() {}.getType();
        List<NotificationItem> list = gson.fromJson(json, type);
        if (list == null) {
            list = new ArrayList<>();
        }

        // Thêm thông báo mới
        list.add(0, item); // thêm đầu danh sách

        // Lưu lại danh sách
        String updatedJson = gson.toJson(list);
        prefs.edit().putString(KEY_NOTIFICATION_LIST, updatedJson).apply();
    }

    public static List<NotificationItem> getNotificationList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_NOTIFICATION_LIST, null);
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

        String json = prefs.getString(KEY_NOTIFICATION_LIST, null);
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
        prefs.edit().putString(KEY_NOTIFICATION_LIST, updatedJson).apply();
    }

}
