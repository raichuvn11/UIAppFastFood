package com.example.uiappfastfood.animation;

import android.view.View;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uiappfastfood.R;

public class OnboardingPageTransformer implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        View imageView = page.findViewById(R.id.imageView);
        View cardView = page.findViewById(R.id.cardView);

        if (position < -1 || position > 1) {
            page.setAlpha(0); // Ẩn hoàn toàn trang ngoài phạm vi
        } else {
            page.setAlpha(1); // Hiển thị trang trong phạm vi
        }

        // Hiệu ứng fade cho hình nền
        if (imageView != null) {
            imageView.setAlpha(1 - Math.abs(position)); // Làm mờ hình nền
        }

        // Hiệu ứng slide cho CardView
        if (cardView != null) {
            float translationX = position * page.getWidth();
            cardView.setTranslationX(translationX);
            cardView.setAlpha(1 - Math.abs(position) * 0.5f); // Làm mờ nhẹ khi trượt
        }
    }
}