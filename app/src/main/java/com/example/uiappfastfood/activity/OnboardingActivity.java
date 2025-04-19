package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uiappfastfood.adapter.OnboardingAdapter;
import com.example.uiappfastfood.model.OnboardingItem;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;
import me.relex.circleindicator.CircleIndicator3;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;
    private List<OnboardingItem> onboardingItems;
    private ImageView imageView;
    private TextView btnSkip, btnNext;
    private ImageView btnFinish;
    private CircleIndicator3 indicator;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Ánh xạ view
        viewPager = findViewById(R.id.viewPager);
        imageView = findViewById(R.id.imageView);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);
        btnFinish = findViewById(R.id.btn_finish);
        indicator = findViewById(R.id.indicator);

        // Danh sách trang onboarding
        onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem("Chào mừng!", "Khám phá tính năng tuyệt vời của ứng dụng.", R.drawable.burger1));
        onboardingItems.add(new OnboardingItem("Tùy chỉnh", "Thiết lập giao diện theo phong cách riêng.", R.drawable.burger2));
        onboardingItems.add(new OnboardingItem("Bắt đầu ngay!", "Trải nghiệm ngay bây giờ.", R.drawable.burger3));

        // Adapter
        adapter = new OnboardingAdapter(onboardingItems);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);

        // Lắng nghe sự kiện chuyển trang
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updateButtonVisibility();
                changeBackgroundWithFade(onboardingItems.get(position).getImageResId());
            }
        });

        // Xử lý sự kiện nút Skip
        btnSkip.setOnClickListener(v -> finishOnboarding());

        // Xử lý sự kiện nút Next
        btnNext.setOnClickListener(v -> {
            if (currentPage < onboardingItems.size() - 1) {
                viewPager.setCurrentItem(currentPage + 1);
            }
        });

        // Xử lý sự kiện nút Finish
        btnFinish.setOnClickListener(v -> finishOnboarding());

        // Hiển thị ảnh nền đầu tiên
        imageView.setImageDrawable(ContextCompat.getDrawable(this, onboardingItems.get(0).getImageResId()));
    }

    // Thay đổi ảnh nền với hiệu ứng fade khi trang thay đổi
    private void changeBackgroundWithFade(int newImageResId) {
        Drawable newDrawable = ContextCompat.getDrawable(this, newImageResId);
        if (newDrawable == null) return;

        imageView.animate().alpha(0f).setDuration(500).withEndAction(() -> {
            imageView.setImageDrawable(newDrawable);
            imageView.animate().alpha(1f).setDuration(500);
        });
    }

    // Ẩn/hiện nút phù hợp
    private void updateButtonVisibility() {
        if (currentPage == onboardingItems.size() - 1) {
            btnNext.setVisibility(View.GONE);
            btnSkip.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnSkip.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.GONE);
        }
    }

    // Kết thúc Onboarding và mở màn hình chính
    private void finishOnboarding() {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(OnboardingActivity.this);

        long userId = sharedPrefManager.getUserId();

        Intent intent;
        if (userId != -1) {
            // Nếu đã đăng nhập (userId không phải -1), chuyển đến MainActivity
            intent = new Intent(this, MainActivity.class);
        } else {
            // Nếu chưa đăng nhập (userId là -1), chuyển đến LoginActivity
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
