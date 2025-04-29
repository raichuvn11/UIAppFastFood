package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

public class OrderDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderItem> orderItemList;
    private TextView tvTotalItems, tvTotalPrice, tvUserName, tvFinalTotalPrice, tvUserPhone, tvOrderAddress, tvOrderStatus;
    private LinearLayout reviewLayout;
    private RatingBar ratingBar;
    private EditText etReview;
    private ApiService apiService;
    private AppCompatButton btnCancelOrder, btnReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        recyclerView = findViewById(R.id.rv_orderItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // mapping ui element
        tvTotalItems = findViewById(R.id.tv_totalItems);
        tvTotalPrice = findViewById(R.id.tv_totalPrice);
        tvFinalTotalPrice = findViewById(R.id.tv_finalTotalPrice);
        tvOrderAddress = findViewById(R.id.tv_orderAddress);
        tvUserName = findViewById(R.id.tv_userName);
        tvUserPhone = findViewById(R.id.tv_userPhone);
        btnCancelOrder = findViewById(R.id.btn_cancelOrder);
        tvOrderStatus = findViewById(R.id.tv_orderStatus);
        ratingBar = findViewById(R.id.ratingBar);
        etReview = findViewById(R.id.et_review);
        btnReview = findViewById(R.id.btn_reviewOrder);
        reviewLayout = findViewById(R.id.reviewLayout);

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
            finish();
        });

        // Hủy đơn hàng
        btnCancelOrder.setOnClickListener(v -> {
            //updateOrder(orderId, "cancelled", 0, "");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_confirm_cancel_order, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();
            dialog.show();
            dialogView.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
            dialogView.findViewById(R.id.btn_confirm).setOnClickListener(view -> {
                updateOrder(orderId, "cancelled", 0, "");
                Intent resultIntent = new Intent();
                resultIntent.putExtra("deletedOrderId", orderId);
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        });

        // đánh giá đơn hàng
        btnReview.setOnClickListener(v -> {
            if (ratingBar.getRating() == 0) {
                Toast.makeText(OrderDetailActivity.this, "Vui lòng đánh giá trước khi gửi!", Toast.LENGTH_SHORT).show();
                return;
            }
            updateOrder(orderId, "delivered", (int) ratingBar.getRating(), etReview.getText().toString());
            finish();
        });
    }
    private void updateOrder(Long orderId, String status, int rating, String review){
        apiService.updateOrder(orderId, status, rating, review).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderDetailActivity.this, "Cập nhật đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Lỗi: " + response.code() + " - " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "Code: " + response.code() + ", Message: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API Failure", "Error: " + t.getMessage());
            }
        });
    }

    private void loadOrderDetails(Long orderId) {
        apiService.getOrderById(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body();
                    orderItemList = new ArrayList<>(order.getOrderItems());
                    orderAdapter = new OrderAdapter(OrderDetailActivity.this, orderItemList);
                    recyclerView.setAdapter(orderAdapter);
                    getOrderDetail(order.getOrderTotal(), order.getOrderAddress(), order.getRating(), order.getReview(), order.getUserName(), order.getUserPhone());

                    switch (order.getOrderStatus()) {
                        case "confirmed":
                            tvOrderStatus.setText("Đã xác nhận");
                            break;
                        case "shipping":
                            tvOrderStatus.setText("Đang giao hàng");
                            btnCancelOrder.setVisibility(View.GONE);
                            break;
                        case "cancelled":
                            tvOrderStatus.setText("Đã hủy");
                            btnCancelOrder.setVisibility(View.GONE);
                            break;
                        case "delivered":
                            tvOrderStatus.setText("Đã giao");
                            reviewLayout.setVisibility(View.VISIBLE);
                            btnCancelOrder.setVisibility(View.GONE);
                            if(order.getRating() != 0){
                                ratingBar.setIsIndicator(true);
                                etReview.setEnabled(false);
                                btnReview.setVisibility(View.GONE);
                            }
                            break;
                        default:
                            tvOrderStatus.setText("Trạng thái không xác định");
                            break;
                    }
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

                Toast.makeText(OrderDetailActivity.this, "Error loading order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrderDetail(double orderTotal, String orderAddress, int rating, String review, String userName, String userPhone){
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
        ratingBar.setRating(rating);
        etReview.setText(review);
        tvUserName.setText(userName);
        tvUserPhone.setText(userPhone);
    }
}
