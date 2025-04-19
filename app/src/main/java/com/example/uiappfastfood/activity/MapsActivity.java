package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.uiappfastfood.DTO.response.AutoComplete;
import com.example.uiappfastfood.DTO.response.AutoCompleteResponse;
import com.example.uiappfastfood.DTO.response.UserResponseDTO;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;
    private AutoCompleteTextView addressAutoCompleteTextView;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker currentMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        addressAutoCompleteTextView = findViewById(R.id.address_edit);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        // Set up AutoCompleteTextView
        addressAutoCompleteTextView.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 2) {  // Khi nhập hơn 2 ký tự
                    suggestAddresses(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(v -> {
            String selectedAddress = addressAutoCompleteTextView.getText().toString().trim();

            if (!selectedAddress.isEmpty()) {
                SharedPrefManager sharedPrefManager = new SharedPrefManager(MapsActivity.this);
                long userId = sharedPrefManager.getUserId();

                if (userId == -1) {
                    Toast.makeText(MapsActivity.this, "Không tìm thấy ID người dùng!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

                Call<UserResponseDTO> call = apiService.updateAddress(userId, selectedAddress);
                call.enqueue(new Callback<UserResponseDTO>() {
                    @Override
                    public void onResponse(Call<UserResponseDTO> call, Response<UserResponseDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(MapsActivity.this, "Cập nhật địa chỉ thành công!", Toast.LENGTH_SHORT).show();


                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("selected_address", selectedAddress);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Toast.makeText(MapsActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponseDTO> call, Throwable t) {
                        Toast.makeText(MapsActivity.this, "Lỗi kết nối API!", Toast.LENGTH_SHORT).show();
                        Log.e("UpdateAddress", "API Error", t);
                    }
                });

            } else {
                Toast.makeText(MapsActivity.this, "Vui lòng chọn địa chỉ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        String address = getIntent().getStringExtra("address");

        if (address.equals("null") || address.isEmpty()) {
            getCurrentLocation();
        } else {
            getCoordinatesFromAddress(address);
        }

        // Di chuyển marker khi di chuyển bản đồ
        mMap.setOnCameraMoveListener(() -> {
            // Lấy tọa độ trung tâm hiện tại của camera
            LatLng centerLatLng = mMap.getCameraPosition().target;

            if (currentMarker != null) {
                // Di chuyển marker hiện tại đến vị trí trung tâm
                currentMarker.setPosition(centerLatLng);
            }
        });

        mMap.setOnCameraIdleListener(() -> {
            // Khi người dùng dừng kéo, cập nhật địa chỉ từ tọa độ trung tâm
            LatLng centerLatLng = mMap.getCameraPosition().target;

            if (currentMarker != null) {
                currentMarker.setPosition(centerLatLng);
            } else {
                // Nếu không có marker, tạo mới marker ở vị trí hiện tại
                currentMarker = mMap.addMarker(new MarkerOptions().position(centerLatLng).title("Vị trí đã chọn"));
            }

            // Cập nhật địa chỉ từ vị trí mới của marker
            getAddressFromLatLng(centerLatLng);
        });

        // Lắng nghe khi người dùng bấm vào bản đồ để chọn vị trí
        mMap.setOnMapClickListener(latLng -> {
            // Xóa marker cũ khi người dùng chọn vị trí mới
            if (currentMarker != null) {
                currentMarker.remove();
            }
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Vị trí đã chọn"));
            getAddressFromLatLng(latLng);
        });
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f));

                            // Đảm bảo chỉ tạo marker một lần khi lấy vị trí hiện tại của người dùng
                            if (currentMarker == null) {
                                currentMarker = mMap.addMarker(new MarkerOptions().position(userLatLng).title("Vị trí của bạn"));
                            }

                            getAddressFromLatLng(userLatLng);
                            mMap.setMyLocationEnabled(true);
                        }
                    });
        }
    }

    private void getAddressFromLatLng(LatLng latLng) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                addressAutoCompleteTextView.setText(address);
            } else {
                addressAutoCompleteTextView.setText("Không tìm được địa chỉ");
            }
        } catch (IOException e) {
            addressAutoCompleteTextView.setText("Lỗi lấy địa chỉ");
            e.printStackTrace();
        }
    }

    private void suggestAddresses(String query) {
        // Kiểm tra nếu query không rỗng
        if (query != null && !query.isEmpty()) {
            // Gọi Goong API để tìm kiếm gợi ý địa chỉ
            ApiService service = RetrofitClient.getRetrofitInstance("https://rsapi.goong.io/")  // Thay bằng URL Goong API
                    .create(ApiService.class);

            Call<AutoCompleteResponse> call = service.getAutoComplete(query, "SkhZNNKpRJ9IXSrJUusq0HPRMgOoKCDKmcLAPe19");  // Đảm bảo thay đúng API key
            call.enqueue(new Callback<AutoCompleteResponse>() {
                @Override
                public void onResponse(Call<AutoCompleteResponse> call, Response<AutoCompleteResponse> response) {
                    if (response != null && response.isSuccessful()) {
                        AutoCompleteResponse data = response.body();
                        if (data != null && data.getPredictions() != null) {
                            List<AutoComplete> autoCompletes = data.getPredictions();
                            List<String> addressSuggestions = new ArrayList<>();
                            for (AutoComplete item : autoCompletes) {
                                addressSuggestions.add(item.getDescription());
                            }

                            // Cập nhật Adapter để hiển thị kết quả gợi ý
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(MapsActivity.this, android.R.layout.simple_dropdown_item_1line, addressSuggestions);
                            addressAutoCompleteTextView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            // Lắng nghe sự kiện khi người dùng chọn một gợi ý
                            addressAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                                String selectedAddress = addressSuggestions.get(position);
                                getCoordinatesFromAddress(selectedAddress);

                                addressAutoCompleteTextView.dismissDropDown();

                                addressAutoCompleteTextView.clearFocus();

                            });
                        } else {
                            Toast.makeText(MapsActivity.this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MapsActivity.this, "Lỗi từ API!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AutoCompleteResponse> call, Throwable t) {
                    Toast.makeText(MapsActivity.this, "Lỗi kết nối đến API!", Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "API call failure", t);
                }
            });
        }
    }

    private void getCoordinatesFromAddress(String address) {
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1); // Tìm 1 kết quả
            if (addressList != null && !addressList.isEmpty()) {
                Address selectedAddress = addressList.get(0);
                LatLng selectedLatLng = new LatLng(selectedAddress.getLatitude(), selectedAddress.getLongitude());

                // Cập nhật vị trí marker
                if (currentMarker != null) {
                    currentMarker.remove(); // Xoá marker cũ
                }

                currentMarker = mMap.addMarker(new MarkerOptions().position(selectedLatLng).title("Vị trí đã chọn"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 16f)); // Di chuyển camera đến vị trí chọn
                addressAutoCompleteTextView.setText(selectedAddress.getAddressLine(0)); // Cập nhật lại text cho AutoCompleteTextView
            } else {
                Toast.makeText(MapsActivity.this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MapsActivity.this, "Lỗi khi tìm kiếm địa chỉ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền vị trí!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
