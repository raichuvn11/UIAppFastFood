package com.example.uiappfastfood.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.OrderAdapter;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.model.Order;
import com.example.uiappfastfood.model.OrderItem;
import com.example.uiappfastfood.api.ApiService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderItem> orderItemList;
    private TextView tvTotalItems, tvTotalPrice, tvUserName, tvFinalTotalPrice, tvUserPhone, tvOrderAddress;
    private ApiService apiService;
    private AppCompatButton btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        recyclerView = findViewById(R.id.rv_orderItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // mapping ui element
        tvTotalItems = findViewById(R.id.tv_totalItems);
        tvTotalPrice = findViewById(R.id.tv_totalPrice);
        tvFinalTotalPrice = findViewById(R.id.tv_finalTotalPrice);
        tvOrderAddress = findViewById(R.id.tv_orderAddress);
        tvUserName = findViewById(R.id.tv_userName);
        tvUserPhone = findViewById(R.id.tv_userPhone);
        btnCheckout = findViewById(R.id.btn_checkout);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Nhận orderId từ Intent
        Long orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId != -1) {
            loadOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Invalid order", Toast.LENGTH_SHORT).show();
        }
        // Quay lại CartActivity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            deleteOrder(orderId);
            finish();
        });

        btnCheckout.setOnClickListener(v -> {
            checkOut(orderId);
        });
    }

    private void loadOrderDetails(Long orderId) {
        apiService.getOrderById(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body();
                    orderItemList = new ArrayList<>(order.getOrderItems());
                    orderAdapter = new OrderAdapter(PaymentActivity.this, orderItemList);
                    recyclerView.setAdapter(orderAdapter);

                    getOrderPayment(order.getOrderTotal(), order.getOrderAddress(), order.getUserName(), order.getUserPhone());
                } else {
                    Toast.makeText(PaymentActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

                Toast.makeText(PaymentActivity.this, "Error loading order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteOrder(Long orderId){
        apiService.deleteOrder(orderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("API delete order", "Code: " + response.code() + ", Message: " + response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API Failure", "Error: " + t.getMessage());
            }
        });
    }

    private void checkOut(Long orderId){
        apiService.updateOrder(orderId, "confirmed", 0, "").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PaymentActivity.this, "Cập nhật đơn hàng thành công!", Toast.LENGTH_SHORT).show();

                    // chuyển tới trang trạng thái đơn hàng
                    Intent intent = new Intent(PaymentActivity.this, OrderStatusActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PaymentActivity.this, "Lỗi: " + response.code() + " - " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "Code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API Failure", "Error: " + t.getMessage());
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void getOrderPayment(double orderTotal, String orderAddress, String userName, String userPhone){
        double totalPrice = 0;
        int totalItem = 0;
        for (OrderItem item : orderItemList) {
            totalItem += item.getQuantity();
            totalPrice += item.getPrice() * item.getQuantity();
        }
        tvTotalItems.setText("Danh sách - " + totalItem + " vật phẩm");
        tvTotalPrice.setText(String.format("%,.0fđ", totalPrice));
        tvOrderAddress.setText(orderAddress);
        tvFinalTotalPrice.setText(String.format("%,.0fđ", orderTotal));
        tvUserName.setText(userName);
        tvUserPhone.setText(userPhone);
    }

}
