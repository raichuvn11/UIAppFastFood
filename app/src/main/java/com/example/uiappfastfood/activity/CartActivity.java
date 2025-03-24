package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.CartAdapter;
import com.example.uiappfastfood.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.rv_cartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartItemList = new ArrayList<>();
        cartItemList.add(new CartItem("Burger With Meat", "$12.230", R.drawable.ic_launcher_foreground, true));
        cartItemList.add(new CartItem("Pizza Margherita", "$8.500", R.drawable.ic_launcher_foreground, true));
        cartItemList.add(new CartItem("French Fries", "$5.000", R.drawable.ic_launcher_foreground, true));
        cartItemList.add(new CartItem("Burger With Meat", "$12.230", R.drawable.ic_launcher_foreground, true));
        cartItemList.add(new CartItem("Pizza Margherita", "$8.500", R.drawable.ic_launcher_foreground, false));
        cartItemList.add(new CartItem("French Fries", "$5.000", R.drawable.ic_launcher_foreground, false));

        cartAdapter = new CartAdapter(this, cartItemList);
        recyclerView.setAdapter(cartAdapter);

        // forward to notification activity
        findViewById(R.id.llNotification).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, NotificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // forward to profile activity
        findViewById(R.id.llProfile).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, ProfileSettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        //back to home activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            //back to home activity
            Toast.makeText(this, "back to home page", Toast.LENGTH_SHORT).show();
        });

        //order
        findViewById(R.id.btnOrder).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }
}
