package com.example.uiappfastfood.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.config.RetrofitClient;
import com.example.uiappfastfood.model.User;
import com.example.uiappfastfood.service.ApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private EditText etStreet, etPhuong, etQuan, etTinh;
    private FusedLocationProviderClient fusedLocationClient;
    private String fullAddress;
    private ApiService apiService;
    private int userId = 1;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        etStreet = findViewById(R.id.et_street);
        etPhuong = findViewById(R.id.et_phuong);
        etQuan = findViewById(R.id.et_quan);
        etTinh = findViewById(R.id.et_tinh);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        getUserLocation();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        findViewById(R.id.btn_currentLocation).setOnClickListener(view -> getCurrentLocation());

        findViewById(R.id.cvBack).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            updateUserProfile();
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();

                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

                                if (!addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    fullAddress = address.getAddressLine(0);
                                    convertLocationToAddress(fullAddress);
                                } else {
                                    Toast.makeText(LocationActivity.this, "Không tìm thấy vị trí", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText(LocationActivity.this, "Không lấy được vị trí", Toast.LENGTH_SHORT).show();
                        }
                });
    }

    @SuppressLint("SetTextI18n")
    private void convertLocationToAddress(String fullAddress) {
        String[] addressParts = fullAddress.split(",");

        if (addressParts.length > 0) {
            etStreet.setText(addressParts[0].trim());
        }
        if (addressParts.length > 1) {
            etPhuong.setText(addressParts[1].trim());
        }
        if (addressParts.length > 2) {
            etQuan.setText(addressParts[2].trim());
        }
        if (addressParts.length > 3) {
            etTinh.setText(addressParts[3].trim());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền vị trí để sử dụng chức năng này", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUserLocation() {
        apiService.getUserProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null){
                    user = response.body();
                    fullAddress = user.getAddress();
                    convertLocationToAddress(fullAddress);
                }
                else{
                    Toast.makeText(LocationActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("PersonalDataActivity", "API Error: " + t.getMessage());
                Toast.makeText(LocationActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        fullAddress = etStreet.getText().toString() + ", " + etPhuong.getText().toString() + ", " + etQuan.getText().toString() + ", " + etTinh.getText().toString();
        user.setAddress(fullAddress);

        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
        RequestBody usernamePart = RequestBody.create(MediaType.parse("text/plain"), user.getUsername());
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), user.getEmail());
        RequestBody phonePart = RequestBody.create(MediaType.parse("text/plain"), user.getPhone());
        RequestBody addressPart = RequestBody.create(MediaType.parse("text/plain"), user.getAddress());
        RequestBody imgPart = RequestBody.create(MediaType.parse("text/plain"), user.getImg());
        MultipartBody.Part file = null;
        apiService.updateUserProfile(idPart, usernamePart, emailPart, phonePart, addressPart, imgPart, file)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(LocationActivity.this, "Cập nhật địa chỉ thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("PersonalDataActivity", "Response error: " + response.code() + " - " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("PersonalDataActivity", "Lỗi khi cập nhật: " + t.getMessage(), t);
                    }
                });
    }
}

