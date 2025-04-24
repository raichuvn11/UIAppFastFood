package com.example.uiappfastfood.sharePreference;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";

    private static final String KEY_TYPE = "type";

    private static final String KEY_CONTEXT = "context";


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveContext(String context) {
        editor.putString(KEY_CONTEXT,context);
        editor.apply();
    }

    public String getShareContext() {
        return sharedPreferences.getString(KEY_CONTEXT, "");
    }

    public void saveUserId(Long userId, String type) {
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_TYPE,type);
        editor.apply();
    }

    public Long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1);
    }
    public String getType() {
        return sharedPreferences.getString(KEY_TYPE, "");
    }

    public void clearUserId() {
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_TYPE);
        editor.apply();
    }
}
