package com.example.uiappfastfood.sharePreference;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserId(Long userId) {
        editor.putLong(KEY_USER_ID, userId);
        editor.apply();
    }

    public Long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1); // -1 nếu chưa có
    }

    public void clearUserId() {
        editor.remove(KEY_USER_ID);
        editor.apply();
    }
}
