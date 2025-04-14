package com.example.uiappfastfood.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.uiappfastfood.fragment.OrderListFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return OrderListFragment.newInstance("confirmed");
            case 1: return OrderListFragment.newInstance("shipping");
            case 2: return OrderListFragment.newInstance("delivered");
            case 3: return OrderListFragment.newInstance("cancelled");
            default: return OrderListFragment.newInstance("Khác");
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Số lượng trạng thái
    }
}

