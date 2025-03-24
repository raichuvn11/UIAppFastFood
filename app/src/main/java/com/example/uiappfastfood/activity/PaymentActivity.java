package com.example.uiappfastfood.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.OrderAdapter;
import com.example.uiappfastfood.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderItem> orderItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        recyclerView = findViewById(R.id.rv_orderItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderItemList = new ArrayList<>();
        orderItemList.add(new OrderItem("Burger With Meat", "$12.230", 1, R.drawable.ic_launcher_foreground));
        orderItemList.add(new OrderItem("Burger With Meat", "$12.230", 1, R.drawable.ic_launcher_foreground));
        orderItemList.add(new OrderItem("Burger With Meat", "$12.230", 2, R.drawable.ic_launcher_foreground));

        orderAdapter = new OrderAdapter(this, orderItemList);
        recyclerView.setAdapter(orderAdapter);

        //back to cart activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            this.finish();
        });
    }
}
