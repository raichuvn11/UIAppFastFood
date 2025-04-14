package com.example.uiappfastfood.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.CartAdapter;
import com.example.uiappfastfood.config.RetrofitClient;
import com.example.uiappfastfood.model.CartItem;
import com.example.uiappfastfood.model.Coupon;
import com.example.uiappfastfood.model.Order;
import com.example.uiappfastfood.model.OrderItem;
import com.example.uiappfastfood.model.User;
import com.example.uiappfastfood.service.ApiService;
import com.example.uiappfastfood.util.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private EditText etPromoCode;
    private TextView tvTotalItems, tvTotalPrice, tvDiscount, tvFinalTotalPrice, tvDeliveryFee, tvOrderAddress, tvNotiCount;
    private ApiService apiService;
    private Coupon coupon;
    private int userId = 1;
    private User user;
    private String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        loadCartItems();
    }

    private void loadCartItems() {
        apiService.getCartItems(userId).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItemList = response.body();
                    if (cartItemList.isEmpty() || cartItemList == null) {
                        switchToEmptyCart();
                    } else {
                        setContentView(R.layout.activity_cart);
                        setupUI();
                    }
                } else {
                    Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e("CartActivity", "API Error: " + t.getMessage());
                Toast.makeText(CartActivity.this, "Error loading cart", Toast.LENGTH_SHORT).show();
                switchToEmptyCart();
            }
        });
    }

    private void setupUI(){
        recyclerView = findViewById(R.id.rv_cartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new CartAdapter(CartActivity.this, cartItemList, CartActivity.this::getPaymentSummary, CartActivity.this::switchToEmptyCart);
        recyclerView.setAdapter(cartAdapter);

        // Mapping UI components
        etPromoCode = findViewById(R.id.etPromoCode);
        tvTotalItems = findViewById(R.id.tv_totalItems);
        tvTotalPrice = findViewById(R.id.tv_totalPrice);
        tvDiscount = findViewById(R.id.tv_discount);
        tvFinalTotalPrice = findViewById(R.id.tv_finalTotalPrice);
        tvDeliveryFee = findViewById(R.id.tv_deliveryFee);
        tvOrderAddress = findViewById(R.id.tv_orderAddress);
        tvNotiCount = findViewById(R.id.tv_notiCount);

        getUserLocation();

        int unreadCount = NotificationUtil.getNewNotification(this);

        if (unreadCount > 0) {
            tvNotiCount.setText(String.valueOf(unreadCount));
        } else {
            tvNotiCount.setVisibility(View.GONE);
        }

        findViewById(R.id.llNotification).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, NotificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.llProfile).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, ProfileSettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.cvBack).setOnClickListener(v -> {
            Toast.makeText(this, "back to home page", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_order).setOnClickListener(v -> {
            createOrder();
        });

        findViewById(R.id.btn_applyCoupon).setOnClickListener(v -> {
            String code = etPromoCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã giảm giá", Toast.LENGTH_SHORT).show();
            }
            apiService.applyCouponCode(code).enqueue(new Callback<Coupon>() {
                @Override
                public void onResponse(Call<Coupon> call, Response<Coupon> response) {
                    if (response.isSuccessful() && response.body() != null){
                        coupon = response.body();
                        getPaymentSummary();
                        Toast.makeText(CartActivity.this, "Mã giảm giá áp dụng thành công", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(CartActivity.this, "Mã giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Coupon> call, Throwable t) {
                    Log.e("CartActivity", "API Coupon Error: " + t.getMessage());
                }
            });

        });

        findViewById(R.id.btn_changeLocation).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, LocationActivity.class);
            startActivity(intent);
        });
    }

    private void switchToEmptyCart() {
        Intent intent = new Intent(this, EmptyCartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private double getPaymentSummary() {
        int totalItems = 0;
        double totalPrice = 0;
        double deliveryFee = 15000; // phí ship mặc định
        for (CartItem item : cartItemList) {
            if (item.isChecked()) {
                totalItems += item.getQuantity();
                totalPrice += item.getQuantity() * item.getPrice();
            }
        }
        double discountAmount = 0;
        if (coupon != null) {
            switch (coupon.getType()) {
                case "FIXED":
                    discountAmount = Math.min(coupon.getDiscount(), totalPrice);
                    break;
                case "PERCENTAGE":
                    discountAmount = totalPrice * coupon.getDiscount() / 100;
                    break;
                case "DELIVERED":
                    deliveryFee -= coupon.getDiscount();
                    break;
            }
        }
        double finalTotalPrice = totalPrice - discountAmount + Math.max(0, deliveryFee);
        tvTotalItems.setText("Total Items (" + totalItems + ")");
        tvTotalPrice.setText(String.format("%,.0fđ", totalPrice));
        tvDeliveryFee.setText(String.format("%,.0fđ", deliveryFee));
        tvDiscount.setText(String.format("%,.0fđ", discountAmount));
        tvFinalTotalPrice.setText(String.format("%,.0fđ", finalTotalPrice));

        return finalTotalPrice;
    }

    private void createOrder() {
        Order order = new Order();
        order.setUserId(Long.parseLong("1"));
        order.setOrderAddress(tvOrderAddress.getText().toString());
        order.setOrderStatus("pending");
        order.setOrderTotal(getPaymentSummary());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItemList) {
            if (item.isChecked()) {
                OrderItem orderItem = new OrderItem(item.getItemId(), item.getName(), item.getPrice(), item.getQuantity(), item.getImg());
                orderItems.add(orderItem);
                Log.d("CartActivity", "Order item added: " + orderItem.getName() + " - Quantity: " + orderItem.getQuantity());
            }
        }
        if (orderItems.isEmpty()) {
            Toast.makeText(this, "Bạn chưa chọn món ăn nào!", Toast.LENGTH_SHORT).show();
            return;
        }

        order.setOrderItems(orderItems);

        apiService.createOrder(order).enqueue(new Callback<Long>() {

            @Override
            public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long orderId = response.body();
                    Toast.makeText(CartActivity.this, "Order created!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                    intent.putExtra("orderId", orderId);
                    startActivity(intent);
                } else {
                    Log.e("PaymentActivity", "API Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (tvOrderAddress != null){
            getUserLocation();
        }

        if (tvNotiCount != null) {
            int unreadCount = NotificationUtil.getNewNotification(this);

            if (unreadCount > 0) {
                tvNotiCount.setVisibility(View.VISIBLE);
                tvNotiCount.setText(String.valueOf(unreadCount));
            } else {
                tvNotiCount.setVisibility(View.GONE);
            }
        }
    }

    private void getUserLocation() {
        apiService.getUserProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null){
                    user = response.body();
                    address = user.getAddress();
                    tvOrderAddress.setText(address);
                }
                else{
                    Toast.makeText(CartActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("PersonalDataActivity", "API Error: " + t.getMessage());
            }
        });
    }
}
