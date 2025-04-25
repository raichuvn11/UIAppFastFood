package com.example.uiappfastfood.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.model.User;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import android.content.Intent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalDataActivity extends AppCompatActivity {
    private ApiService apiService;
    private TextView tvTitleUserName, tvTitleEmail;
    private EditText etUserName, etEmail, etPhone, etAddress;
    private ImageView imgUser;
    private CardView cardView;
    private AppCompatButton btnSave;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private User user;
    private int userId;
    private boolean hasSelectedNewImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        userId = sharedPrefManager.getUserId().intValue();

        tvTitleUserName = findViewById(R.id.tv_titleUserName);
        tvTitleEmail = findViewById(R.id.tv_titleEmail);

        etUserName = findViewById(R.id.et_userName);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        imgUser = findViewById(R.id.img_user);
        cardView = findViewById(R.id.cv_upload_avatar);
        btnSave = findViewById(R.id.btn_save);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        loadUserData(userId);

        //back to previous activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            finish();
        });

        //upload avatar
        cardView.setOnClickListener(v -> {
            openGallery();
        });

        //save data
        btnSave.setOnClickListener(v -> {
            updateUserProfile(userId);
        });

        etAddress.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalDataActivity.this, LocationActivity.class);
            startActivity(intent);
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                imgUser.setImageURI(imageUri);
                hasSelectedNewImage = true;
                Log.d("PersonalDataActivity", "Image URI: " + imageUri.toString());
            } else {
                Log.e("PersonalDataActivity", "Image URI is null!");
            }
        }
    }

    private void loadUserData(int userId){

        apiService.getUserProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null){
                    user = response.body();
                    tvTitleUserName.setText(user.getUsername());
                    tvTitleEmail.setText(user.getEmail());

                    etUserName.setText(user.getUsername());
                    etEmail.setText(user.getEmail());
                    etPhone.setText(user.getPhone());
                    etAddress.setText(user.getAddress());

                    if (!hasSelectedNewImage && user.getImg() != null) {
                        Glide.with(PersonalDataActivity.this)
                                .load(RetrofitClient.BASE_URL + user.getImg())
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(R.drawable.ic_launcher_foreground)
                                .into(imgUser);
                    }
                }
                else{
                    Toast.makeText(PersonalDataActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("PersonalDataActivity", "API Error: " + t.getMessage());
                Toast.makeText(PersonalDataActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value != null ? value : "");
    }

    private void updateUserProfile(int userId) {
        // Lấy dữ liệu từ các EditText
        user.setUsername(etUserName.getText().toString().trim());
        user.setEmail(etEmail.getText().toString().trim());
        user.setPhone(etPhone.getText().toString().trim());
        user.setAddress(etAddress.getText().toString().trim());

        RequestBody idPart = toRequestBody(String.valueOf(userId));
        RequestBody usernamePart = toRequestBody(user.getUsername());
        RequestBody emailPart = toRequestBody(user.getEmail());
        RequestBody phonePart = toRequestBody(user.getPhone());
        RequestBody addressPart = toRequestBody(user.getAddress());
        RequestBody imgPart = toRequestBody(user.getImg());

        MultipartBody.Part file = null;
        if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream); // Nén với chất lượng 50%
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();

                    String mimeType = getContentResolver().getType(imageUri);
                    if (mimeType == null) mimeType = "image/jpeg";

                    RequestBody fileBody = RequestBody.create(MediaType.parse(mimeType), imageBytes);
                    String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
                    file = MultipartBody.Part.createFormData("file", fileName, fileBody);

                    inputStream.close();
                    byteArrayOutputStream.close();
                } else {
                    Log.e("PersonalDataActivity", "Không thể mở luồng đầu vào cho URI: " + imageUri);
                    Toast.makeText(this, "Không thể xử lý ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (IOException e) {
                Log.e("PersonalDataActivity", "Lỗi khi xử lý ảnh: " + e.getMessage(), e);
                Toast.makeText(this, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        apiService.updateUserProfile(idPart, usernamePart, emailPart, phonePart, addressPart, imgPart, file)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PersonalDataActivity.this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                            hasSelectedNewImage = false;
                            loadUserData(userId);
                        } else {
                            Toast.makeText(PersonalDataActivity.this, "Cập nhật hồ sơ thất bại", Toast.LENGTH_SHORT).show();
                            Log.e("PersonalDataActivity", "Response error: " + response.code() + " - " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("PersonalDataActivity", "Lỗi khi cập nhật: " + t.getMessage(), t);
                        Toast.makeText(PersonalDataActivity.this, "Lỗi khi cập nhật hồ sơ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(userId);
    }
}
