package com.example.uiappfastfood.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.uiappfastfood.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Kiểm tra quyền vị trí
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            initializePlaces();
        }
    }

    private void initializePlaces() {
        // Khởi tạo Google Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBXhAdrVHYZk_JQioQvOrf1foSEaQM4fcQ");
        }
        placesClient = Places.createClient(this);
    }

    // Gọi API Places khi người dùng nhập vào một địa chỉ
    private void findAddress(String query) {
        // Tạo yêu cầu để tìm kiếm địa điểm
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setCountry("VN") // Bạn có thể thay đổi quốc gia tùy theo yêu cầu
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener((FindAutocompletePredictionsResponse response) -> {
                    // Xử lý kết quả trả về
                    List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
                    for (AutocompletePrediction prediction : predictions) {
                        String placeName = prediction.getFullText(null).toString();
                        Toast.makeText(AddressActivity.this, "Suggested Place: " + placeName, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener((Exception e) -> {
                    // Xử lý lỗi khi yêu cầu thất bại
                    Toast.makeText(AddressActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Xử lý kết quả yêu cầu quyền truy cập vị trí
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializePlaces();  // Nếu cấp quyền, khởi tạo Places API
            } else {
                Toast.makeText(this, "Permission denied, cannot access location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
