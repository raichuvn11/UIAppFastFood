package com.example.uiappfastfood.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.DTO.request.VnpayRequest;
import com.example.uiappfastfood.DTO.response.VnpayResponse;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.OrderAdapter;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.model.Order;
import com.example.uiappfastfood.model.OrderItem;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.util.NetworkUtil;

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
    private RadioButton btnCod, btnOnline;
    private double orderTotal = 0;

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
        btnCod = findViewById(R.id.rb_cod);
        btnOnline = findViewById(R.id.rb_online);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Nhận orderId từ Intent
        Long orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId != -1) {
            loadOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Dữ liệu đơn hàng không hợp lệ", Toast.LENGTH_SHORT).show();
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
                    orderTotal = order.getOrderTotal();
                    orderItemList = new ArrayList<>(order.getOrderItems());
                    orderAdapter = new OrderAdapter(PaymentActivity.this, orderItemList);
                    recyclerView.setAdapter(orderAdapter);

                    getOrderPayment(order.getOrderTotal(), order.getOrderAddress(), order.getUserName(), order.getUserPhone());
                } else {
                    Toast.makeText(PaymentActivity.this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

                Toast.makeText(PaymentActivity.this, "Tải đơn hàng thất bại", Toast.LENGTH_SHORT).show();
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
        if (btnCod.isChecked()){
            updateOrder(orderId);
        }else if (btnOnline.isChecked()) {
            VnpayRequest request = new VnpayRequest((int) orderTotal, "NCB", "vn", NetworkUtil.getDeviceIPAddress(PaymentActivity.this), orderId);
            apiService.createVnPayUrl(request).enqueue(new Callback<VnpayResponse>() {
                @Override
                public void onResponse(Call<VnpayResponse> call, Response<VnpayResponse> response) {
                    if (response.isSuccessful() && response.body() != null && "00".equals(response.body().getCode())) {
                        // Mở trình duyệt đến URL thanh toán
                        String paymentUrl = response.body().getData();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
                        startActivity(intent);
                    } else {
                        Toast.makeText(PaymentActivity.this, "Lỗi tạo URL thanh toán", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<VnpayResponse> call, Throwable t) {
                    Toast.makeText(PaymentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(PaymentActivity.this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
        }

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

    private void updateOrder(Long orderId) {
        apiService.updateOrder(orderId, "confirmed", 0, "").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PaymentActivity.this, "Cập nhật đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PaymentActivity.this, OrderStatusActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PaymentActivity.this, "Lỗi cập nhật đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Uri data = intent.getData();
        if (data != null && data.getPath() != null && data.getPath().startsWith("/result")) {
            String responseCode = data.getQueryParameter("vnp_ResponseCode");
            String orderIdStr = data.getQueryParameter("vnp_TxnRef");

            if (orderIdStr != null && responseCode != null) {
                Long orderId = Long.parseLong(orderIdStr);
                if ("00".equals(responseCode)) {
                    updateOrder(orderId);
                } else {
                    Toast.makeText(this, "Thanh toán thất bại!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
