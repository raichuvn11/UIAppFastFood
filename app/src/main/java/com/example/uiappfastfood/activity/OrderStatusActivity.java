package com.example.uiappfastfood.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.OrderPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderStatusActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private OrderPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new OrderPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("Xác nhận"); break;
                        case 1: tab.setText("Đang giao"); break;
                        case 2: tab.setText("Đã giao"); break;
                        case 3: tab.setText("Đã hủy"); break;
                    }
                }
        ).attach();

        int selectedTab = getIntent().getIntExtra("selectedTab", 0);
        viewPager.setCurrentItem(selectedTab, false);

        //back to previous activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            finish();
        });
    }
}
