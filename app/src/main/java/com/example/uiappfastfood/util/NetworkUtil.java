package com.example.uiappfastfood.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkUtil {
    @SuppressLint("DefaultLocale")
    public static String getDeviceIPAddress(Context context) {
        try {
            // Lấy thông tin kết nối Wi-Fi
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

