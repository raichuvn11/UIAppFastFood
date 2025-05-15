package com.example.uiappfastfood.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.CartAdapter;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.model.CartItem;
import com.example.uiappfastfood.model.Coupon;
import com.example.uiappfastfood.model.Order;
import com.example.uiappfastfood.model.OrderItem;
import com.example.uiappfastfood.model.User;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;
import com.example.uiappfastfood.activity.LocationActivity;
import com.example.uiappfastfood.activity.PaymentActivity;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private EditText etPromoCode;
    private TextView tvTotalItems, tvTotalPrice, tvDiscount, tvFinalTotalPrice, tvDeliveryFee, tvOrderAddress, tvNotiCount;
    private ApiService apiService;
    private Coupon coupon;
    private int userId;
    private User user;
    private String address = "";
    private ConstraintLayout mainLayout;
    private LinearLayout emptyLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
        userId = sharedPrefManager.getUserId().intValue();
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        initViews(view);
        loadCartItems();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_cartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        etPromoCode = view.findViewById(R.id.etPromoCode);
        tvTotalItems = view.findViewById(R.id.tv_totalItems);
        tvTotalPrice = view.findViewById(R.id.tv_totalPrice);
        tvDiscount = view.findViewById(R.id.tv_discount);
        tvFinalTotalPrice = view.findViewById(R.id.tv_finalTotalPrice);
        tvDeliveryFee = view.findViewById(R.id.tv_deliveryFee);
        tvOrderAddress = view.findViewById(R.id.tv_orderAddress);
        mainLayout = view.findViewById(R.id.main_layout);
        emptyLayout = view.findViewById(R.id.empty_cart);

        view.findViewById(R.id.btn_order).setOnClickListener(v -> createOrder());

        view.findViewById(R.id.btn_applyCoupon).setOnClickListener(v -> {
            String code = etPromoCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập mã giảm giá", Toast.LENGTH_SHORT).show();
                return;
            }
            apiService.applyCouponCode(code).enqueue(new Callback<Coupon>() {
                @Override
                public void onResponse(Call<Coupon> call, Response<Coupon> response) {
                    if (response.isSuccessful() && response.body() != null){
                        coupon = response.body();
                        getPaymentSummary();
                        Toast.makeText(getContext(), "Mã giảm giá áp dụng thành công", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Mã giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Coupon> call, Throwable t) {
                    Log.e("CartFragment", "API Coupon Error: " + t.getMessage());
                }
            });
        });

        view.findViewById(R.id.btn_changeLocation).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LocationActivity.class);
            startActivity(intent);
        });
    }

    private void loadCartItems() {
        apiService.getCartItems(userId).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItemList = response.body();
                    if (cartItemList.isEmpty()) {
                        switchToEmptyCart();
                    } else {
                        cartAdapter = new CartAdapter(getContext(), cartItemList, CartFragment.this::getPaymentSummary, CartFragment.this::switchToEmptyCart);
                        recyclerView.setAdapter(cartAdapter);
                        getPaymentSummary();
                    }
                } else {
                    Log.e("CartFragment", "API Error: " + response.errorBody());
                    switchToEmptyCart();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e("CartFragment", "API Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error loading cart", Toast.LENGTH_SHORT).show();
                switchToEmptyCart();
            }
        });
    }

    private void switchToEmptyCart() {
        mainLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private double getPaymentSummary() {
        int totalItems = 0;
        double totalPrice = 0;
        double deliveryFee = 15000;

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
        tvTotalItems.setText("Tổng cộng (tạm tính) - " + totalItems + " vật phẩm");
        tvTotalPrice.setText(String.format("%,.0fđ", totalPrice));
        tvDeliveryFee.setText(String.format("%,.0fđ", deliveryFee));
        tvDiscount.setText(String.format("%,.0fđ", discountAmount));
        tvFinalTotalPrice.setText(String.format("%,.0fđ", finalTotalPrice));

        return finalTotalPrice;
    }

    private void createOrder() {
        Order order = new Order();
        order.setUserId((long) userId);
        order.setOrderAddress(tvOrderAddress.getText().toString());
        order.setOrderStatus("pending");
        order.setOrderTotal(getPaymentSummary());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItemList) {
            if (item.isChecked()) {
                OrderItem orderItem = new OrderItem(item.getItemId(), item.getName(), item.getPrice(), item.getQuantity(), item.getImg());
                orderItems.add(orderItem);
            }
        }

        if (orderItems.isEmpty()) {
            Toast.makeText(getContext(), "Bạn chưa chọn món ăn nào!", Toast.LENGTH_SHORT).show();
            return;
        }

        order.setOrderItems(orderItems);

        apiService.createOrder(order).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long orderId = response.body();

                    Intent intent = new Intent(getActivity(), PaymentActivity.class);
                    intent.putExtra("orderId", orderId);
                    startActivity(intent);
                } else {
                    Log.e("CartFragment", "API Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tvOrderAddress != null) {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        apiService.getUserProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    address = user.getAddress();
                    tvOrderAddress.setText(address);
                } else {
                    Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("CartFragment", "API Error: " + t.getMessage());
            }
        });
    }
}
